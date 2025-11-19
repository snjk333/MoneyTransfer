package com.oleksandr.moneytransfer.dto.Request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequest(

        @NotNull
        String fromWalletNumber,

        @NotNull
        String toWalletNumber,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal amount
)
{ }
