package com.wallet.local.client.service;


import com.wallet.local.client.rest.model.response.TransferResponse;
import com.wallet.local.lib.BalanceRequest;
import com.wallet.local.lib.Currency;
import com.wallet.local.lib.TransferRequest;
import com.wallet.local.lib.WalletServiceGrpc;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class WalletClientService {

    @GrpcClient("wallet-local-server")
    private WalletServiceGrpc.WalletServiceBlockingStub walletServiceBlockingStub;

    private static final Logger log = LoggerFactory.getLogger(WalletClientService.class);
    private static final List<String> allowedCurrencies = Arrays.asList("EUR", "USD", "GBP");

    public TransferResponse deposit(Long userId, Float amount, String currency) {
        TransferResponse response = new TransferResponse();
        try {

            if (!isCurrencyValid(currency)) {
                log.warn("Unknown currency - {}", currency);
                response.setSuccess(false);
                response.setMessage("unknown_currency");
                return response;
            }

            var depositRequest = TransferRequest.newBuilder()
                    .setUserId(userId)
                    .setAmount(amount)
                    .setCurrency(Currency.valueOf(currency))
                    .build();

            walletServiceBlockingStub.deposit(depositRequest);
            response.setSuccess(true);
            response.setMessage("Ok");
            return response;

        } catch (final StatusRuntimeException e) {
            log.warn("GRPC server exception: " + e.getStatus().getCode().name() + " -- " + e.getStatus().getDescription());

            response.setSuccess(false);
            response.setMessage(e.getStatus().getDescription());
            return response;
        } catch (final Exception e) {
            log.error("Unexpected exception:" + e.getLocalizedMessage());
            throw e;
        }
    }

    public TransferResponse withdraw(Long userId, Float amount, String currency) {
        TransferResponse response = new TransferResponse();
        try {

            if (!isCurrencyValid(currency)) {
                log.warn("Unknown currency - {}", currency);
                response.setMessage("unknown_currency");
                response.setSuccess(false);
                return response;
            }

            var withdrawRequest = TransferRequest.newBuilder()
                    .setUserId(userId)
                    .setAmount(amount)
                    .setCurrency(Currency.valueOf(currency))
                    .build();

            walletServiceBlockingStub.withdraw(withdrawRequest);
            response.setSuccess(true);
            response.setMessage("Ok");
            return response;

        } catch (final StatusRuntimeException e) {
            log.warn("GRPC server exception: " + e.getStatus().getCode().name() + " -- " + e.getStatus().getDescription());

            response.setSuccess(false);
            response.setMessage(e.getStatus().getDescription());
            return response;
        } catch (final Exception e) {
            log.error("Unexpected exception:" + e.getLocalizedMessage());
            throw e;
        }
    }

    public Map<String, Float> balance(Long userId) {

        try {
            var balanceRequest = BalanceRequest.newBuilder().setUserId(userId).build();

            var response = walletServiceBlockingStub.balance(balanceRequest);
            return response.getBalanceMap();

        } catch (final StatusRuntimeException e) {
            log.warn("GRPC server exception: " + e.getStatus().getCode().name() + " -- " + e.getStatus().getDescription());
            throw e;
        } catch (final Exception e) {
            log.error("Unexpected exception:" + e.getLocalizedMessage());
            throw e;
        }

    }

    private boolean isCurrencyValid(String currency) {
        return allowedCurrencies.contains(currency);
    }
}