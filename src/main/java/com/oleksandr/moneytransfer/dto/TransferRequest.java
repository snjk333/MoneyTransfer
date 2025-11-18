package com.oleksandr.moneytransfer.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest(
        UUID fromWalletId,
        UUID toWalletId,
        BigDecimal amount
)
{ }
