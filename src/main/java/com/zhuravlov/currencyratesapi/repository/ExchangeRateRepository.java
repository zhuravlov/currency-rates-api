package com.zhuravlov.currencyratesapi.repository;

import com.zhuravlov.currencyratesapi.model.ExchangeRate;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ExchangeRateRepository extends CrudRepository<ExchangeRate, Long> {

    @Query(value = """
        select * 
        from exchange_rates 
        where id in 
            (select max(id) 
             from exchange_rates 
             where base_currency_code = :currencyBase 
             group by target_currency_code) 
            """)
    Collection<ExchangeRate> findLatestRates(String currencyBase);
}
