package com.oleksandr.moneytransfer.rest;

import com.oleksandr.moneytransfer.dto.request.WalletCreateRequest;
import com.oleksandr.moneytransfer.dto.response.WalletResponse;
import com.oleksandr.moneytransfer.service.interfaces.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@RequestBody @Valid WalletCreateRequest request) {
         WalletResponse walletResponse = walletService.createWallet(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(walletResponse);
    }

    @GetMapping("/{number}")
    public ResponseEntity<WalletResponse> getByNumber(@PathVariable String number) {
        WalletResponse walletResponse = walletService.getByNumber(number);
        return ResponseEntity.ok(walletResponse);
    }

}
