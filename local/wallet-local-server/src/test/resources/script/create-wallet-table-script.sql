create table if not exists wallet
(
    id       int auto_increment primary key,
    user_id  int            not null,
    amount   decimal(10, 2) not null,
    currency varchar(3)     not null
);