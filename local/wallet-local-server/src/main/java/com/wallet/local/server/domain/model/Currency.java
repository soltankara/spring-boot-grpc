package com.wallet.local.server.domain.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "currency")
public class Currency {

    @Id
    @GeneratedValue
    Long id;

    @Column(name = "currency")
    String currency;

    @Column(name = "is_enabled")
    Boolean isEnabled;
}
