package com.wallet.local.server.domain.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue
    Long id;

    @Column(name = "username")
    String username;
}
