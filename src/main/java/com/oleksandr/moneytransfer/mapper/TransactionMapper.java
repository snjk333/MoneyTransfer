package com.oleksandr.moneytransfer.mapper;

import com.oleksandr.moneytransfer.dto.TransactionResponse;
import com.oleksandr.moneytransfer.entity.Transaction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class TransactionMapper {
    public TransactionResponse toResponse(Transaction transaction, String status) {

        if (transaction == null || status == null) {
            return null;
        }

        return new TransactionResponse(
                transaction.getId(),
                transaction.getFromWallet().getId(),
                transaction.getToWallet().getId(),
                transaction.getAmount(),
                status
        );
    }
}
