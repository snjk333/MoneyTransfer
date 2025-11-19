package com.oleksandr.moneytransfer.mapper;

import com.oleksandr.moneytransfer.dto.responce.TransactionResponse;
import com.oleksandr.moneytransfer.entity.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {
    public TransactionResponse toResponse(Transaction transaction, String status) {

        if (transaction == null || status == null) {
            return null;
        }

        return new TransactionResponse(
                transaction.getId(),
                transaction.getFromWallet().getNumber(),
                transaction.getToWallet().getNumber(),
                transaction.getAmount(),
                status
        );
    }
}
