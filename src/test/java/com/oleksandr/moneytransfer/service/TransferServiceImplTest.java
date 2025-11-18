package com.oleksandr.moneytransfer.service;

import com.oleksandr.moneytransfer.dto.TransactionResponse;
import com.oleksandr.moneytransfer.dto.TransferRequest;
import com.oleksandr.moneytransfer.entity.Currency;
import com.oleksandr.moneytransfer.entity.Transaction;
import com.oleksandr.moneytransfer.entity.Wallet;
import com.oleksandr.moneytransfer.exceptions.CurrencyMismatchException;
import com.oleksandr.moneytransfer.exceptions.InsufficientFundsException;
import com.oleksandr.moneytransfer.exceptions.SelfTransferException;
import com.oleksandr.moneytransfer.mapper.TransactionMapper;
import com.oleksandr.moneytransfer.repository.TransactionRepository;
import com.oleksandr.moneytransfer.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransferServiceImplTest {

    @Mock
    WalletRepository walletRepository;

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    TransactionMapper transactionMapper;

    @InjectMocks
    TransferServiceImpl transferService;


    @Test
    void shouldSuccessfullyExceedTransfer() {
        // GIVEN
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        BigDecimal initialBalance = new BigDecimal("100.00");
        BigDecimal amountToTransfer = new BigDecimal("50.00");

        Wallet senderWallet = Wallet.builder()
                .id(senderId)
                .balance(initialBalance)
                .currency(Currency.USD)
                .build();
        Wallet receiverWallet = Wallet.builder()
                .id(receiverId)
                .balance(initialBalance)
                .currency(Currency.USD)
                .build();

        TransferRequest request = new TransferRequest(
                senderId,
                receiverId,
                amountToTransfer
        );

        when(walletRepository.findByIdWithLock(senderId)).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findByIdWithLock(receiverId)).thenReturn(Optional.of(receiverWallet));

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, Transaction.class));

        when(transactionMapper.toResponse(any(Transaction.class), eq("SUCCESS")))
                .thenReturn(new TransactionResponse(
                        UUID.randomUUID(),
                        senderId,
                        receiverId,
                        amountToTransfer,
                        "SUCCESS")
                );

        TransactionResponse response = transferService.transfer(request);

        assertNotNull(response);
        assertEquals(amountToTransfer, response.amount());
        assertEquals(senderId, response.fromWalletId());
        assertEquals(receiverId, response.toWalletId());
        assertEquals("SUCCESS", response.status());

        assertEquals(new BigDecimal("50.00"), senderWallet.getBalance());
        assertEquals(new BigDecimal("150.00"), receiverWallet.getBalance());

        verify(walletRepository, times(1)).findByIdWithLock(senderId);
        verify(walletRepository, times(1)).findByIdWithLock(receiverId);

        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(walletRepository, times(1)).save(senderWallet);
        verify(walletRepository, times(1)).save(receiverWallet);
    }

    @Test
    void shouldThrowExceptionWhenInsufficientFunds() {
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        BigDecimal largeAmount = new BigDecimal("1000.00");

        Wallet sender = Wallet.builder()
                .id(senderId)
                .balance(new BigDecimal("100.00"))
                .currency(Currency.USD)
                .build();

        TransferRequest request = new TransferRequest(
                senderId,
                receiverId,
                largeAmount
        );

        when(walletRepository.findByIdWithLock(senderId)).thenReturn(Optional.of(sender));
        when(walletRepository.findByIdWithLock(receiverId)).thenReturn(Optional.of(
                Wallet.builder().id(receiverId).balance(BigDecimal.ZERO).currency(Currency.USD).build()));

        // WHEN & THEN
        assertThrows(InsufficientFundsException.class, () -> {
            transferService.transfer(request);
        });

        verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenCurrencyMismatch() {
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("50.00");

        //USD
        Wallet sender = Wallet.builder()
                .id(senderId)
                .balance(new BigDecimal("100.00"))
                .currency(Currency.USD)
                .build();

        //EUR
        Wallet receiver = Wallet.builder()
                .id(receiverId)
                .balance(new BigDecimal("10.00"))
                .currency(Currency.EUR)
                .build();

        TransferRequest request = new TransferRequest(
                senderId,
                receiverId,
                amount
        );

        when(walletRepository.findByIdWithLock(senderId)).thenReturn(Optional.of(sender));
        when(walletRepository.findByIdWithLock(receiverId)).thenReturn(Optional.of(receiver));

        // WHEN & THEN
        assertThrows(CurrencyMismatchException.class, () -> {
            transferService.transfer(request);
        });

        verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenSenderIsReceiver() {
        UUID walletId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("10.00");

        TransferRequest request = new TransferRequest(walletId, walletId, amount);

        assertThrows(SelfTransferException.class, () -> {
            transferService.transfer(request);
        });

        verify(walletRepository, never()).findByIdWithLock(any());
        verify(transactionRepository, never()).save(any());
    }
}
