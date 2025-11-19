package com.oleksandr.moneytransfer.service;

import com.oleksandr.moneytransfer.dto.response.TransactionResponse;
import com.oleksandr.moneytransfer.dto.request.TransferRequest;

public interface TransferService {
    TransactionResponse transfer(TransferRequest request);
}