package com.oleksandr.moneytransfer.mapper;

import com.oleksandr.moneytransfer.dto.Responce.WalletResponse;
import com.oleksandr.moneytransfer.entity.Wallet;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class WalletMapper {

    public WalletResponse toResponse(Wallet wallet) {
        if (wallet == null) {
            return null;
        }
        return new WalletResponse(
                wallet.getId(),
                wallet.getNumber(),
                wallet.getBalance(),
                wallet.getCurrency()
        );
    }

    public List<WalletResponse> toResponseList(List<Wallet> wallets) {
        if (wallets == null) {
            return Collections.emptyList();
        }
        return wallets.stream()
                .map(this::toResponse)
                .toList();
    }
}
