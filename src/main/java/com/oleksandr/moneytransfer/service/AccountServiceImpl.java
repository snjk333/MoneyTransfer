package com.oleksandr.moneytransfer.service;

import com.oleksandr.moneytransfer.dto.request.AccountCreationRequest;
import com.oleksandr.moneytransfer.dto.responce.AccountResponse;
import com.oleksandr.moneytransfer.entity.Account;
import com.oleksandr.moneytransfer.mapper.AccountMapper;
import com.oleksandr.moneytransfer.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    public AccountResponse createAccount(AccountCreationRequest request) {
        Account account = accountMapper.toCreateEntity(request);
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toResponse(savedAccount);
    }
}
