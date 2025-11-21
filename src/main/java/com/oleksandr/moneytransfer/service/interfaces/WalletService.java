package com.oleksandr.moneytransfer.service.interfaces;

import com.oleksandr.moneytransfer.dto.request.WalletCreateRequest;
import com.oleksandr.moneytransfer.dto.response.WalletResponse;
import jakarta.validation.Valid;

public interface WalletService {
    WalletResponse createWallet(@Valid WalletCreateRequest walletCreateRequest);
    WalletResponse getByNumber(@Valid String number);
}
