package com.oleksandr.moneytransfer.service.implementation;

import com.oleksandr.moneytransfer.dto.response.TransactionResponse;
import com.oleksandr.moneytransfer.dto.request.TransferRequest;
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
import org.mockito.InOrder;
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
        String senderNumber = "10001";
        String receiverNumber = "10002";
        BigDecimal initialBalance = new BigDecimal("100.00");
        BigDecimal amountToTransfer = new BigDecimal("50.00");

        Wallet senderWallet = Wallet.builder()
                .id(senderId)
                .number(senderNumber)
                .balance(initialBalance)
                .currency(Currency.USD)
                .build();
        Wallet receiverWallet = Wallet.builder()
                .id(receiverId)
                .number(receiverNumber)
                .balance(initialBalance)
                .currency(Currency.USD)
                .build();

        TransferRequest request = new TransferRequest(
                senderNumber,
                receiverNumber,
                amountToTransfer
        );

        when(walletRepository.findByNumberWithLock(senderNumber)).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findByNumberWithLock(receiverNumber)).thenReturn(Optional.of(receiverWallet));

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, Transaction.class));

        when(transactionMapper.toResponse(any(Transaction.class), eq("SUCCESS")))
                .thenReturn(new TransactionResponse(
                        UUID.randomUUID(),
                        senderNumber,
                        receiverNumber,
                        amountToTransfer,
                        "SUCCESS")
                );

        TransactionResponse response = transferService.transfer(request);

        assertNotNull(response);
        assertEquals(amountToTransfer, response.amount());

        assertEquals(senderNumber, response.fromWalletNumber());
        assertEquals(receiverNumber, response.toWalletNumber());

        assertEquals("SUCCESS", response.status());

        assertEquals(new BigDecimal("50.00"), senderWallet.getBalance());
        assertEquals(new BigDecimal("150.00"), receiverWallet.getBalance());

        verify(walletRepository, times(1)).findByNumberWithLock(senderNumber);
        verify(walletRepository, times(1)).findByNumberWithLock(receiverNumber);

        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(walletRepository, times(1)).save(senderWallet);
        verify(walletRepository, times(1)).save(receiverWallet);
    }

    @Test
    void shouldThrowExceptionWhenInsufficientFunds() {
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        String senderNumber = "10001";
        String receiverNumber = "10002";
        BigDecimal largeAmount = new BigDecimal("1000.00");

        Wallet sender = Wallet.builder()
                .id(senderId)
                .number(senderNumber)
                .balance(new BigDecimal("100.00"))
                .currency(Currency.USD)
                .build();

        TransferRequest request = new TransferRequest(
                senderNumber,
                receiverNumber,
                largeAmount
        );

        when(walletRepository.findByNumberWithLock(senderNumber)).thenReturn(Optional.of(sender));
        when(walletRepository.findByNumberWithLock(receiverNumber)).thenReturn(Optional.of(
                Wallet.builder().id(receiverId).number(receiverNumber).balance(BigDecimal.ZERO)
                        .currency(Currency.USD).build()));

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
        String senderNumber = "10001";
        String receiverNumber = "10002";
        BigDecimal amount = new BigDecimal("50.00");

        //USD
        Wallet sender = Wallet.builder()
                .id(senderId)
                .number(senderNumber)
                .balance(new BigDecimal("100.00"))
                .currency(Currency.USD)
                .build();

        //EUR
        Wallet receiver = Wallet.builder()
                .id(receiverId)
                .number(receiverNumber)
                .balance(new BigDecimal("10.00"))
                .currency(Currency.EUR)
                .build();

        TransferRequest request = new TransferRequest(
                senderNumber,
                receiverNumber,
                amount
        );

        when(walletRepository.findByNumberWithLock(senderNumber)).thenReturn(Optional.of(sender));
        when(walletRepository.findByNumberWithLock(receiverNumber)).thenReturn(Optional.of(receiver));

        // WHEN & THEN
        assertThrows(CurrencyMismatchException.class, () -> {
            transferService.transfer(request);
        });

        verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenSenderIsReceiver() {
        String walletNumber = "10001";
        BigDecimal amount = new BigDecimal("10.00");

        TransferRequest request = new TransferRequest(walletNumber, walletNumber, amount);

        assertThrows(SelfTransferException.class, () -> {
            transferService.transfer(request);
        });

        verify(walletRepository, never()).findByNumberWithLock(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldLockWalletsInConsistentOrderToPreventDeadlocks() {
        //idA_Smaller < idB_Larger
        UUID idA_Smaller = new UUID(0L, 1L);
        UUID idB_Larger = new UUID(0L, 2L);

        String numberA_Smaller = "10000";
        String numberB_Larger = "90009";

        TransferRequest request = new TransferRequest(numberB_Larger, numberA_Smaller, BigDecimal.TEN);

        when(walletRepository.findByNumberWithLock(numberA_Smaller))
                .thenReturn(Optional.of(Wallet.builder().id(idA_Smaller).number(numberA_Smaller).balance(BigDecimal.TEN).currency(Currency.USD).build()));
        when(walletRepository.findByNumberWithLock(numberB_Larger))
                .thenReturn(Optional.of(Wallet.builder().id(idB_Larger).number(numberB_Larger).balance(BigDecimal.TEN).currency(Currency.USD).build()));

        when(transactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionMapper.toResponse(any(), anyString())).thenReturn(new TransactionResponse(idA_Smaller, numberA_Smaller, numberB_Larger, BigDecimal.TEN, "SUCCESS"));


        InOrder inOrder = inOrder(walletRepository);

        // WHEN
        transferService.transfer(request);

        // THEN
        inOrder.verify(walletRepository).findByNumberWithLock(numberA_Smaller);
        inOrder.verify(walletRepository).findByNumberWithLock(numberB_Larger);

        verify(walletRepository, times(2)).findByNumberWithLock(any());
    }
}
