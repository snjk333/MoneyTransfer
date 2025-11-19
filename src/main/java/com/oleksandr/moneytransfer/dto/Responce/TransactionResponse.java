package com.oleksandr.moneytransfer.dto.Responce;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionResponse(
        UUID transactionId,
        String fromWalletNumber,
        String toWalletNumber,
        BigDecimal amount,
        String status
) {}
