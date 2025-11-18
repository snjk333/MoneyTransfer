package com.oleksandr.moneytransfer.dto;

import java.util.List;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String firstName,
        String lastName,
        List<WalletResponse> wallets
) { }
