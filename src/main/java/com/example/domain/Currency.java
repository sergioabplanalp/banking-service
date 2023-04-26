package com.example.domain;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

public enum Currency {
    EUR, GBP, USD, ZAR;

    public CurrencyUnit toUnit() {
        return Monetary.getCurrency(this.name());
    }

    public static CurrencyUnit getDefault() {
        return Currency.EUR.toUnit();
    }
}
