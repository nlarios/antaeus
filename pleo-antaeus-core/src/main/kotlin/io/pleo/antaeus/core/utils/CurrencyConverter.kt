package io.pleo.antaeus.core.utils

import io.pleo.antaeus.core.providers.ExchangeRateProvider
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Money
import java.math.BigDecimal


internal fun convertCurrency(currencyFrom: Currency, currencyTo: Currency = Currency.DKK, amount: BigDecimal): Money {
    return if (currencyFrom == ExchangeRateProvider.exchangeRate.base) {
        var rate: BigDecimal = ExchangeRateProvider.exchangeRate.rates.getValue(currencyTo)

        Money(
                value = amount * rate,
                currency = currencyTo
        )
    } else {
        var rate: BigDecimal = ExchangeRateProvider.exchangeRate.rates.getValue(currencyFrom)
        Money(
                value = amount / rate,
                currency = ExchangeRateProvider.exchangeRate.base
        )
    }
}



