package com.oleksandr.moneytransfer.mapper;

import com.oleksandr.moneytransfer.dto.AccountResponse;
import com.oleksandr.moneytransfer.entity.Account;
import com.oleksandr.moneytransfer.entity.Currency;
import com.oleksandr.moneytransfer.entity.Wallet;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

class AccountMapperTest {
    private final WalletMapper walletMapper = new WalletMapper();
    public final AccountMapper accountMapper = new AccountMapper(walletMapper);

    @Test
    void shouldMapAccountToResponseDto(){
        UUID accountId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();

        Wallet wallet = Wallet.builder()
                .id(walletId)
                .balance(new BigDecimal("100.0000"))
                .currency(Currency.USD)
                .build();

        Account account = Account.builder()
                .id(accountId)
                .firstName("Olek")
                .lastName("Kulbit")
                .wallets(List.of(wallet))
                .build();

        AccountResponse response = accountMapper.toResponce(account);


        assertNotNull(response);
        assertEquals(accountId, response.id());
        assertEquals("Olek", response.firstName());
        assertEquals("Kulbit", response.lastName());

        assertEquals(1, response.wallets().size());
        assertEquals(walletId, response.wallets().get(0).id());
        assertEquals(new BigDecimal("100.0000"), response.wallets().get(0).balance());
    }
}
