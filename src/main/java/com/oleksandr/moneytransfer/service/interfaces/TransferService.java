package com.oleksandr.moneytransfer.service.interfaces;

import com.oleksandr.moneytransfer.dto.response.TransactionResponse;
import com.oleksandr.moneytransfer.dto.request.TransferRequest;

public interface TransferService {
    TransactionResponse transfer(TransferRequest request);
}