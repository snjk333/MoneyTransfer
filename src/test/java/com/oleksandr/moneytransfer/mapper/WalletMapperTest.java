package com.oleksandr.moneytransfer.mapper;

import com.oleksandr.moneytransfer.dto.response.WalletResponse;
import com.oleksandr.moneytransfer.entity.Currency;
import com.oleksandr.moneytransfer.entity.Wallet;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WalletMapperTest {

    private final WalletMapper walletMapper = new WalletMapper();

    @Test
    void shouldMapWalletToResponse() {
        String WalletNum = "10001";
        UUID walletId = UUID.randomUUID();
        Wallet wallet = Wallet.builder()
                .id(walletId)
                .number(WalletNum)
                .balance(new BigDecimal("100.000"))
                .currency(Currency.USD)
                .build();


        WalletResponse response = walletMapper.toResponse(wallet);

        assertNotNull(response);
        assertEquals(walletId, response.id());
        assertEquals(WalletNum, response.number());
        assertEquals(new BigDecimal("100.000"), response.balance());
        assertEquals(Currency.USD, response.currency());
    }

    @Test
    void shouldMapWalletListToResponseList(){
        Wallet w1 = Wallet.builder()
                .id(UUID.randomUUID())
                .number("10001")
                .balance(BigDecimal.TEN)
                .currency(Currency.USD)
                .build();

        Wallet w2 = Wallet.builder()
                .id(UUID.randomUUID())
                .number("10002")
                .balance(BigDecimal.ONE)
                .currency(Currency.EUR)
                .build();
        List<Wallet> wallets = List.of(w1, w2);

        List<WalletResponse> responses = walletMapper.toResponseList(wallets);

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(w1.getId(), responses.get(0).id());
        assertEquals(w2.getId(), responses.get(1).id());
    }

    @Test
    void shouldReturnEmptyListWhenInputIsNull() {
        List<WalletResponse> responses = walletMapper.toResponseList(null);

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }



}