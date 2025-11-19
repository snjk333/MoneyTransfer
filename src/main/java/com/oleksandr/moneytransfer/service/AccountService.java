package com.oleksandr.moneytransfer.service;

import com.oleksandr.moneytransfer.dto.request.AccountCreationRequest;
import com.oleksandr.moneytransfer.dto.responce.AccountResponse;
import jakarta.validation.Valid;

public interface AccountService {
    AccountResponse createAccount(@Valid AccountCreationRequest request);
}
