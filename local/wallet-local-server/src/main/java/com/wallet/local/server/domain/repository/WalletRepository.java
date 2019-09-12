package com.wallet.local.server.domain.repository;

import com.wallet.local.server.domain.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long>, CustomWalletRepository {
}
