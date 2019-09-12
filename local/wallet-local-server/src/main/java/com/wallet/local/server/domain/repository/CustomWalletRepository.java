package com.wallet.local.server.domain.repository;

import com.wallet.local.server.domain.model.Wallet;

import java.util.List;

public interface CustomWalletRepository {

    Wallet findByUserIdAndCurrency(Long userId, String currency);

    List<Wallet> getBalanceByUserId(Long userId);
}
