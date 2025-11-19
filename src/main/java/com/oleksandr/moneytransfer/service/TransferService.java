package com.oleksandr.moneytransfer.service;

import com.oleksandr.moneytransfer.dto.Responce.TransactionResponse;
import com.oleksandr.moneytransfer.dto.Request.TransferRequest;

public interface TransferService {
    TransactionResponse transfer(TransferRequest request);
}