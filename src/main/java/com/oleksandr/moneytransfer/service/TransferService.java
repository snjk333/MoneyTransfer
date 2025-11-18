package com.oleksandr.moneytransfer.service;

import com.oleksandr.moneytransfer.dto.TransactionResponse;
import com.oleksandr.moneytransfer.dto.TransferRequest;

public interface TransferService {
    TransactionResponse transfer(TransferRequest request);
}