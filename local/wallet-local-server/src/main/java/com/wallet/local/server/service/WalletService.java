package com.wallet.local.server.service;

import com.wallet.local.lib.*;
import com.wallet.local.server.domain.model.Wallet;
import com.wallet.local.server.domain.repository.WalletRepository;
import com.wallet.local.server.error.ErrorType;
import com.wallet.local.server.exception.InsufficientFundsException;
import com.wallet.local.server.exception.UnknownCurrencyException;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@GrpcService
public class WalletService extends WalletServiceGrpc.WalletServiceImplBase {

    @Autowired
    WalletRepository repository;

    private static final Logger log = LoggerFactory.getLogger(WalletService.class);
    private static final List<String> allowedCurrencies = Arrays.asList("EUR", "USD", "GBP");


    @Override
    public void deposit(TransferRequest request, StreamObserver<TransferResponse> responseObserver) {

        if (!isCurrencyValid(request.getCurrency().name())) {
            log.warn("Unknown currency - {}", request.getCurrency().name());
            var exception = new UnknownCurrencyException("Unknown currency: " + request.getCurrency().name());
            throwException(responseObserver, exception, ErrorType.UNKNOWN_CURRENCY, Status.INVALID_ARGUMENT);

        } else {

            var wallet = repository.findByUserIdAndCurrency(request.getUserId(), request.getCurrency().name());
            if (wallet == null) {
                wallet = new Wallet(Long.valueOf(request.getUserId()), new BigDecimal(0), request.getCurrency().name());
            }

            var amount = wallet.getAmount().add(new BigDecimal(request.getAmount()));
            wallet.setAmount(amount);

            repository.save(wallet);

            var response = TransferResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void withdraw(TransferRequest request, StreamObserver<TransferResponse> responseObserver) {

        if (!isCurrencyValid(request.getCurrency().name())) {
            log.warn("Unknown currency - {}", request.getCurrency().name());
            var exception = new UnknownCurrencyException("Unknown currency: " + request.getCurrency().name());

            throwException(responseObserver, exception, ErrorType.UNKNOWN_CURRENCY, Status.INVALID_ARGUMENT);

        } else {

            var wallet = repository.findByUserIdAndCurrency(request.getUserId(), request.getCurrency().name());
            if (wallet == null || wallet.getAmount().compareTo(new BigDecimal(request.getAmount())) == -1) {
                log.warn("Insufficient funds - user id {}, currency {}", request.getUserId(), request.getCurrency().name());
                var exception = new InsufficientFundsException("Insufficient funds: user id -> " + request.getUserId() + " currency -> " + request.getCurrency().name());

                throwException(responseObserver, exception, ErrorType.INSUFFICIENT_FUNDS, Status.UNAVAILABLE);
            } else {
                var amount = wallet.getAmount().subtract(new BigDecimal(request.getAmount()));

                wallet.setAmount(amount);
                repository.save(wallet);

                var response = TransferResponse.newBuilder().build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }

        }
    }

    @Override
    public void balance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {

        var response = BalanceResponse.newBuilder();

        var userBalance = repository.getBalanceByUserId(request.getUserId());

        Map<String, Float> map = new HashMap<>();
        for (Wallet wallet : userBalance) {
            map.put(wallet.getCurrency(), wallet.getAmount().floatValue());
        }

        response.putAllBalance(map);

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    private boolean isCurrencyValid(String currency) {
        return allowedCurrencies.contains(currency);
    }

    private void throwException(StreamObserver<?> responseObserver, Exception exception, String errorType, Status status) {
        responseObserver.onError(status
                .withDescription(errorType)
                .augmentDescription(errorType)
                .withCause(exception)
                .asRuntimeException());
    }
}
