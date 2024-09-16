package com.zhuravlov.currencyratesapi.repository;

import com.zhuravlov.currencyratesapi.model.ObservableCurrency;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ObservableCurrencyRepository  extends CrudRepository<ObservableCurrency, Long> {

    @Override
    Collection<ObservableCurrency> findAll();
}
