package com.oleksandr.moneytransfer.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionResponse(
        UUID transactionId,
        UUID fromWalletId,
        UUID toWalletId,
        BigDecimal amount,
        String status
) {}
