package com.oleksandr.moneytransfer.service;

import com.oleksandr.moneytransfer.dto.request.AccountCreationRequest;
import com.oleksandr.moneytransfer.dto.responce.AccountResponse;
import com.oleksandr.moneytransfer.dto.responce.TransactionResponse;
import com.oleksandr.moneytransfer.entity.Account;
import com.oleksandr.moneytransfer.entity.Transaction;
import com.oleksandr.moneytransfer.mapper.AccountMapper;
import com.oleksandr.moneytransfer.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    AccountRepository accountRepository;

    @Mock
    AccountMapper accountMapper;

    @InjectMocks
    AccountServiceImpl accountService;

    @Test
    void shouldCreateAccount() {
        // GIVEN
        AccountCreationRequest request = new AccountCreationRequest("Olek", "Sandr");

        Account accountToSave = Account.builder()
                .firstName("Olek")
                .lastName("Sandr")
                .wallets(new ArrayList<>())
                .build();

        UUID generatedId = UUID.randomUUID();
        Account savedAccount = Account.builder()
                .id(generatedId)
                .firstName("Olek")
                .lastName("Sandr")
                .wallets(new ArrayList<>())
                .build();

        AccountResponse expectedResponse = new AccountResponse(
                generatedId,
                "Olek",
                "Sandr",
                Collections.emptyList()
        );


        when(accountMapper.toCreateEntity(request)).thenReturn(accountToSave);
        when(accountRepository.save(accountToSave)).thenReturn(savedAccount);
        when(accountMapper.toResponse(savedAccount)).thenReturn(expectedResponse);

        // WHEN
        AccountResponse response = accountService.createAccount(request);

        // THEN
        assertNotNull(response);
        assertEquals("Olek", response.firstName());
        assertEquals("Sandr", response.lastName());
        assertEquals(generatedId, response.id());

        verify(accountRepository).save(accountToSave);
        verify(accountMapper).toResponse(savedAccount);
    }
}