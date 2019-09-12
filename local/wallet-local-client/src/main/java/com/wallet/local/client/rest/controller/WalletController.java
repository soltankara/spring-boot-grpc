package com.wallet.local.client.rest.controller;

import com.wallet.local.client.rest.model.response.TransferResponse;
import com.wallet.local.client.service.WalletClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/wallet-local-client")
public class WalletController {

    @Autowired
    WalletClientService service;

    @GetMapping(value = "/deposit/{userId}/{amount}/{currency}")
    public ResponseEntity<TransferResponse> deposit(
            @PathVariable Long userId,
            @PathVariable Float amount,
            @PathVariable String currency
    ) {
        return ResponseEntity.ok(service.deposit(userId, amount, currency));
    }

    @GetMapping(value = "/withdraw/{userId}/{amount}/{currency}")
    public ResponseEntity<TransferResponse> withdraw(
            @PathVariable Long userId,
            @PathVariable Float amount,
            @PathVariable String currency
    ) {
        return ResponseEntity.ok(service.withdraw(userId, amount, currency));
    }


    @GetMapping(value = "/balance/{userId}")
    public ResponseEntity<Map<String, Float>> balance(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(service.balance(userId));
    }
}
