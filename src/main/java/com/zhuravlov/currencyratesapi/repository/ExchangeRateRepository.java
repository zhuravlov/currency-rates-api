package com.zhuravlov.currencyratesapi.repository;

import com.zhuravlov.currencyratesapi.model.ExchangeRate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeRateRepository extends CrudRepository<ExchangeRate, Long> {

}
