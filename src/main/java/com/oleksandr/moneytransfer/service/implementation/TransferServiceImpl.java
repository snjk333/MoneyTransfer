package com.oleksandr.moneytransfer.service.implementation;

import com.oleksandr.moneytransfer.dto.response.TransactionResponse;
import com.oleksandr.moneytransfer.dto.request.TransferRequest;
import com.oleksandr.moneytransfer.entity.Transaction;
import com.oleksandr.moneytransfer.entity.Wallet;
import com.oleksandr.moneytransfer.exceptions.CurrencyMismatchException;
import com.oleksandr.moneytransfer.exceptions.InsufficientFundsException;
import com.oleksandr.moneytransfer.exceptions.SelfTransferException;
import com.oleksandr.moneytransfer.exceptions.WalletNotFoundException;
import com.oleksandr.moneytransfer.mapper.TransactionMapper;
import com.oleksandr.moneytransfer.repository.TransactionRepository;
import com.oleksandr.moneytransfer.repository.WalletRepository;
import com.oleksandr.moneytransfer.service.interfaces.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public TransactionResponse transfer(TransferRequest request) {

        if(request.fromWalletNumber().equals(request.toWalletNumber())) {
            throw new SelfTransferException("Self transfer failed");
        }

        final String lockId1;
        final String lockId2;

        if (request.fromWalletNumber().compareTo(request.toWalletNumber()) < 0) {
            lockId1 = request.fromWalletNumber();
            lockId2 = request.toWalletNumber();
        } else {
            lockId1 = request.toWalletNumber();
            lockId2 = request.fromWalletNumber();
        }

        Wallet wallet1 = walletRepository.findByNumberWithLock(lockId1)
                .orElseThrow(() -> new WalletNotFoundException("Wallet " + lockId1 + " not found."));

        Wallet wallet2 = walletRepository.findByNumberWithLock(lockId2)
                .orElseThrow(() -> new WalletNotFoundException("Wallet " + lockId2 + " not found."));

        Wallet fromWallet = wallet1.getNumber().equals(request.fromWalletNumber()) ? wallet1 : wallet2;
        Wallet toWallet = wallet1.getNumber().equals(request.toWalletNumber()) ? wallet1 : wallet2;

        if(fromWallet.getId().equals(toWallet.getId())) {
            throw new IllegalStateException("Database consistency failure. Different wallet numbers map to the same ID.");
        }

        if(!fromWallet.getCurrency().equals(toWallet.getCurrency())) {
            throw new CurrencyMismatchException("Currency mismatch");
        }

        if(fromWallet.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientFundsException("Not enough money to transfer");
        }

        fromWallet.setBalance(fromWallet.getBalance().subtract(request.amount()));
        toWallet.setBalance(toWallet.getBalance().add(request.amount()));

        Transaction transaction = new Transaction(
                request.amount(),
                fromWallet,
                toWallet
        );
        transactionRepository.save(transaction);
        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);

        return transactionMapper.toResponse(transaction, "SUCCESS");

    }
}
