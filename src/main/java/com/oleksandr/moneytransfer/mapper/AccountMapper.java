package com.oleksandr.moneytransfer.mapper;

import com.oleksandr.moneytransfer.dto.AccountResponse;
import com.oleksandr.moneytransfer.dto.WalletResponse;
import com.oleksandr.moneytransfer.entity.Account;
import com.oleksandr.moneytransfer.entity.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountMapper {
    private final WalletMapper walletMapper;

    public AccountResponse toResponce(Account account) {

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
}
