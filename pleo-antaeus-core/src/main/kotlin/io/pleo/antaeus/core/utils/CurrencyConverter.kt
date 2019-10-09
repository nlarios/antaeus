package io.pleo.antaeus.core.utils

import io.pleo.antaeus.core.providers.ExchangeRateProvider
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Money
import java.math.BigDecimal


internal fun convertCurrency(currencyFrom: Currency, currencyTo: Currency = Currency.DKK, amount: BigDecimal ): Money {
    var rate: BigDecimal = ExchangeRateProvider.exchangeRate.rates.getValue(currencyFrom)

    return Money(
            value = amount * rate,
            currency = ExchangeRateProvider.exchangeRate.base
    )
}



