package com.oleksandr.moneytransfer.service;

import com.oleksandr.moneytransfer.dto.responce.TransactionResponse;
import com.oleksandr.moneytransfer.dto.request.TransferRequest;

public interface TransferService {
    TransactionResponse transfer(TransferRequest request);
}