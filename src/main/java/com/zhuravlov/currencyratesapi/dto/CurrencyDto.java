package com.zhuravlov.currencyratesapi.dto;

public class CurrencyDto {

    private String code;

    public CurrencyDto(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
