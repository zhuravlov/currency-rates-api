insert into observable_currencies (currency_code)
values ('EUR'),
       ('USD');

insert into exchange_rates (base_currency_code, target_currency_code, rate, traded_at)
values ('EUR', 'USD', 1.1, now() AT TIME ZONE 'UTC'),
       ('EUR', 'PLN', 4.1, now() AT TIME ZONE 'UTC'),
       ('EUR', 'UAH', 41.5, now() AT TIME ZONE 'UTC'),

       ('USD', 'EUR', 0.9, now() AT TIME ZONE 'UTC'),
       ('USD', 'PLN', 4.5, now() AT TIME ZONE 'UTC'),
       ('USD', 'UAH', 45.0, now() AT TIME ZONE 'UTC');