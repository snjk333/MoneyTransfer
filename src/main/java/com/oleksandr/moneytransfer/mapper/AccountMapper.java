package com.oleksandr.moneytransfer.mapper;

import com.oleksandr.moneytransfer.dto.request.AccountCreationRequest;
import com.oleksandr.moneytransfer.dto.response.AccountResponse;
import com.oleksandr.moneytransfer.dto.response.WalletResponse;
import com.oleksandr.moneytransfer.entity.Account;
import com.oleksandr.moneytransfer.entity.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountMapper {
    private final WalletMapper walletMapper;

    public AccountResponse toResponse(Account account) {

        if (account == null) {
            return null;
        }

        List<Wallet> wallets = account.getWallets();
        List<WalletResponse> walletResponses = walletMapper.toResponseList(wallets);

        return new AccountResponse(
                account.getId(),
                account.getFirstName(),
                account.getLastName(),
                walletResponses
        );
    }

    public Account toCreateEntity(AccountCreationRequest request) {

        if (request == null) {
            return null;
        }
        return Account.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .wallets(new ArrayList<>())
                .build();
    }
}
