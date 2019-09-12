create table wallet
(
    id       int auto_increment
        primary key,
    user_id  int            not null,
    amount   decimal(10, 2) not null,
    currency varchar(3)     not null
);

---------

INSERT INTO walletdb.wallet (id, user_id, amount, currency) VALUES (1, 1, 100.00, 'USD');
INSERT INTO walletdb.wallet (id, user_id, amount, currency) VALUES (2, 2, 122.22, 'EUR');
INSERT INTO walletdb.wallet (id, user_id, amount, currency) VALUES (3, 2, 3923.22, 'USD');
INSERT INTO walletdb.wallet (id, user_id, amount, currency) VALUES (4, 1, 1232.42, 'GBP');