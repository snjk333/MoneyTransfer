package com.oleksandr.moneytransfer.rest;

import com.oleksandr.moneytransfer.dto.request.TransferRequest;
import com.oleksandr.moneytransfer.dto.response.TransactionResponse;
import com.oleksandr.moneytransfer.service.interfaces.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<TransactionResponse> transfer(@RequestBody @Valid TransferRequest request) {
        TransactionResponse response = transferService.transfer(request);
        return ResponseEntity.ok(response);
    }
}