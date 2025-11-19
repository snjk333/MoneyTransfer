package com.oleksandr.moneytransfer.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequest(

        @NotNull(message = "Sender wallet number is required")
        String fromWalletNumber,

        @NotNull(message = "Receiver wallet number is required")
        String toWalletNumber,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01")
        BigDecimal amount
)
{ }
