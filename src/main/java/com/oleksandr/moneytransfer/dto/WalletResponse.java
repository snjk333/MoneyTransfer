package com.oleksandr.moneytransfer.dto;

import com.oleksandr.moneytransfer.entity.Currency;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletResponse(
        UUID id,
        BigDecimal balance,
        Currency currency
) { }
