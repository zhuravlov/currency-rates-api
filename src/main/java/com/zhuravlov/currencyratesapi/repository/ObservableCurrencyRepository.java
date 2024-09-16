package com.zhuravlov.currencyratesapi.repository;

import com.zhuravlov.currencyratesapi.model.ObservableCurrency;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObservableCurrencyRepository  extends CrudRepository<ObservableCurrency, Long> {

}
