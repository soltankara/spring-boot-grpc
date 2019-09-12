package com.wallet.local.server.domain.repository;

import com.wallet.local.server.domain.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

public class WalletRepositoryImpl implements CustomWalletRepository {

    @Autowired
    EntityManager em;

    @Override
    public Wallet findByUserIdAndCurrency(Long userId, String currency) {
        try {
            return em.createQuery("select wallet from Wallet wallet where wallet.userId = ?1 and wallet.currency = ?2", Wallet.class)
                    .setParameter(1, userId)
                    .setParameter(2, currency)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Wallet> getBalanceByUserId(Long userId) {
        return em.createQuery("select wallet from Wallet wallet where wallet.userId = ?1", Wallet.class)
                .setParameter(1, userId)
                .getResultList();
    }

}
