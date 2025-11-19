package com.oleksandr.moneytransfer.service.implementation;

import com.oleksandr.moneytransfer.dto.request.AccountCreationRequest;
import com.oleksandr.moneytransfer.dto.response.AccountResponse;
import com.oleksandr.moneytransfer.entity.Account;
import com.oleksandr.moneytransfer.mapper.AccountMapper;
import com.oleksandr.moneytransfer.repository.AccountRepository;
import com.oleksandr.moneytransfer.service.interfaces.AccountService;
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
