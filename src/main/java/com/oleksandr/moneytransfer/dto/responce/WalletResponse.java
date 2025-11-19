package com.oleksandr.moneytransfer.dto.responce;

import com.oleksandr.moneytransfer.entity.Currency;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletResponse(
        UUID id,
        String number,
        BigDecimal balance,
        Currency currency
) { }
