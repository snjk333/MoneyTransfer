package com.oleksandr.moneytransfer.service.implementation;

import com.oleksandr.moneytransfer.dto.request.WalletCreateRequest;
import com.oleksandr.moneytransfer.dto.response.WalletResponse;
import com.oleksandr.moneytransfer.entity.Account;
import com.oleksandr.moneytransfer.entity.Wallet;
import com.oleksandr.moneytransfer.exceptions.WalletNotFoundException;
import com.oleksandr.moneytransfer.mapper.WalletMapper;
import com.oleksandr.moneytransfer.repository.AccountRepository;
import com.oleksandr.moneytransfer.repository.WalletRepository;
import com.oleksandr.moneytransfer.service.interfaces.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;
    private final AccountRepository accountRepository;

    private final SecureRandom random = new SecureRandom();

    @Override
    @Transactional
    public WalletResponse createWallet(WalletCreateRequest request) {

        Account account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new RuntimeException("Account not found with ID: " + request.accountId()));

        String accountNumber = generateUniqueNumber();

        Wallet wallet = Wallet.builder()
                .number(accountNumber)
                .currency(request.currency())
                .balance(BigDecimal.ZERO)
                .owner(account)
                .build();

        Wallet savedWallet = walletRepository.save(wallet);

        return walletMapper.toResponse(savedWallet);
    }

    @Override
    public WalletResponse getByNumber(String number) {
        Wallet wallet = walletRepository.findByNumber(number)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found with number: " + number));

        return walletMapper.toResponse(wallet);
    }


    private String generateUniqueNumber() {
        String number;
        int attempts = 0;
        do {
            if (attempts > 5) {
                throw new RuntimeException("Failed to generate unique number. System overloaded.");
            }
            int num = 10000 + random.nextInt(90000);
            number = String.valueOf(num);
            attempts++;
        } while (walletRepository.existsByNumber(number));

        return number;
    }
}