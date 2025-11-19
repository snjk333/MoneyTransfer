package com.oleksandr.moneytransfer.dto.responce;

import java.util.List;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String firstName,
        String lastName,
        List<WalletResponse> wallets
) { }
