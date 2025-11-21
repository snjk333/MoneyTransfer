package com.oleksandr.moneytransfer.dto.request;

import com.oleksandr.moneytransfer.entity.Currency;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record WalletCreateRequest(

        @NotNull(message = "Account ID is required")
        UUID accountId,

        @NotNull(message = "Currency is required")
        Currency currency

) { }
