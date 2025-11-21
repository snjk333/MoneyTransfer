package com.oleksandr.moneytransfer.service.implementation;

import static org.junit.jupiter.api.Assertions.*;

import com.oleksandr.moneytransfer.dto.request.WalletCreateRequest;
import com.oleksandr.moneytransfer.dto.response.WalletResponse;
import com.oleksandr.moneytransfer.entity.Account;
import com.oleksandr.moneytransfer.entity.Currency;
import com.oleksandr.moneytransfer.entity.Wallet;
import com.oleksandr.moneytransfer.exceptions.WalletNotFoundException;
import com.oleksandr.moneytransfer.mapper.WalletMapper;
import com.oleksandr.moneytransfer.repository.AccountRepository;
import com.oleksandr.moneytransfer.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private WalletMapper walletMapper;

    @InjectMocks
    private WalletServiceImpl walletService;

    @Test
    void shouldCreateWalletSuccessfully() {
        UUID accountId = UUID.randomUUID();
        WalletCreateRequest request = new WalletCreateRequest(accountId, Currency.USD);
        Account account = new Account();
        account.setId(accountId);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(walletRepository.existsByNumber(anyString())).thenReturn(false);
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArgument(0));
        when(walletMapper.toResponse(any(Wallet.class))).thenReturn(
                new WalletResponse(UUID.randomUUID(), "12345", BigDecimal.ZERO, Currency.USD)
        );

        WalletResponse response = walletService.createWallet(request);


        assertNotNull(response);
        verify(accountRepository).findById(accountId);
        verify(walletRepository).existsByNumber(anyString());
        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    void shouldThrowExceptionWhenAccountNotFound() {
        UUID accountId = UUID.randomUUID();
        WalletCreateRequest request = new WalletCreateRequest(accountId, Currency.USD);

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> walletService.createWallet(request));

        verify(walletRepository, never()).save(any());
    }

    @Test
    void shouldRetryGenerationIfNumberExists() {
        // GIVEN
        UUID accountId = UUID.randomUUID();
        WalletCreateRequest request = new WalletCreateRequest(accountId, Currency.USD);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(new Account()));

        when(walletRepository.existsByNumber(anyString()))
                .thenReturn(true)
                .thenReturn(false);

        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArgument(0));
        when(walletMapper.toResponse(any())).thenReturn(new WalletResponse(null, "55555", BigDecimal.ZERO, Currency.USD));

        walletService.createWallet(request);

        verify(walletRepository, times(2)).existsByNumber(anyString());
        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    void shouldGetByNumberSuccessfully() {
        String number = "12345";
        Wallet wallet = new Wallet();

        when(walletRepository.findByNumber(number)).thenReturn(Optional.of(wallet));
        when(walletMapper.toResponse(wallet)).thenReturn(new WalletResponse(UUID.randomUUID(), number, BigDecimal.TEN, Currency.USD));

        WalletResponse response = walletService.getByNumber(number);

        assertNotNull(response);
        assertEquals(number, response.number());
    }

    @Test
    void shouldThrowExceptionWhenWalletNotFound() {
        String number = "99999";
        when(walletRepository.findByNumber(number)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> walletService.getByNumber(number));
    }
}