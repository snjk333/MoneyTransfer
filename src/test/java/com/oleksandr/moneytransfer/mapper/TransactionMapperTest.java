package com.oleksandr.moneytransfer.mapper;

import com.oleksandr.moneytransfer.dto.Responce.TransactionResponse;
import com.oleksandr.moneytransfer.entity.Currency;
import com.oleksandr.moneytransfer.entity.Transaction;
import com.oleksandr.moneytransfer.entity.Wallet;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransactionMapperTest {

    TransactionMapper transactionMapper = new TransactionMapper();

    @Test
    void toResponse() {
        UUID transactionId = UUID.randomUUID();

        UUID fromWalletId = UUID.randomUUID();
        UUID toWalletId = UUID.randomUUID();

        Wallet walletFrom = Wallet.builder()
                .id(fromWalletId)
                .number("10001")
                .balance(new BigDecimal("100.0000"))
                .currency(Currency.USD)
                .build();

        Wallet walletTo = Wallet.builder()
                .id(toWalletId)
                .number("10002")
                .balance(new BigDecimal("100.0000"))
                .currency(Currency.USD)
                .build();

        Transaction transaction = Transaction.builder()
                .id(transactionId)
                .amount(new BigDecimal("100.0000"))
                .fromWallet(walletFrom)
                .toWallet(walletTo)
                .build();


        TransactionResponse response = transactionMapper.toResponse(transaction, "SUCCESS");

        assertNotNull(response);
        assertEquals(transactionId, response.transactionId());
        assertEquals("10001", response.fromWalletNumber());
        assertEquals("10002", response.toWalletNumber());
        assertEquals(new BigDecimal("100.0000"), response.amount());

        assertEquals("SUCCESS", response.status());
    }
}