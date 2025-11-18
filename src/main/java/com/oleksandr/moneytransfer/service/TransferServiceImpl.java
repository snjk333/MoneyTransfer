package com.oleksandr.moneytransfer.service;

import com.oleksandr.moneytransfer.dto.TransactionResponse;
import com.oleksandr.moneytransfer.dto.TransferRequest;
import com.oleksandr.moneytransfer.entity.Transaction;
import com.oleksandr.moneytransfer.entity.Wallet;
import com.oleksandr.moneytransfer.exceptions.CurrencyMismatchException;
import com.oleksandr.moneytransfer.exceptions.InsufficientFundsException;
import com.oleksandr.moneytransfer.exceptions.SelfTransferException;
import com.oleksandr.moneytransfer.exceptions.WalletNotFoundException;
import com.oleksandr.moneytransfer.mapper.TransactionMapper;
import com.oleksandr.moneytransfer.repository.TransactionRepository;
import com.oleksandr.moneytransfer.repository.WalletRepository;
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

        if(request.fromWalletId().equals(request.toWalletId())) {
            throw new SelfTransferException("Self transfer failed");
        }

        Wallet fromWallet = walletRepository.findByIdWithLock(request.fromWalletId())
                .orElseThrow(() -> new WalletNotFoundException("Sender wallet not found."));

        Wallet toWallet = walletRepository.findByIdWithLock(request.toWalletId())
                .orElseThrow(() -> new WalletNotFoundException("Receiver wallet not found."));

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
