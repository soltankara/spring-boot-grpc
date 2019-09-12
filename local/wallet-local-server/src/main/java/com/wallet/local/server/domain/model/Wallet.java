package com.wallet.local.server.domain.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "wallet")
public class Wallet implements Serializable {

    private static final long serialVersionUID = -995633628447810601L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "user_id", nullable = false)
    Long userId;

    @Column(name = "amount")
    BigDecimal amount;

    @Column(name = "currency", nullable = false)
    String currency;

    public Wallet (Long user, BigDecimal amount, String currency) {
        this.setUserId(user);
        this.setAmount(amount);
        this.setCurrency(currency);
    }

    public Wallet() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
