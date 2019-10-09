package io.pleo.antaeus.core.providers

import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.ExchangeRate
import java.math.BigDecimal

object ExchangeRateProvider {

    val exchangeRate = ExchangeRate(
            base = Currency.DKK,
            rates = hashMapOf(
                    Currency.DKK to setRates(Currency.DKK),
                    Currency.EUR to setRates(Currency.EUR),
                    Currency.GBP to setRates(Currency.GBP),
                    Currency.SEK to setRates(Currency.SEK),
                    Currency.USD to setRates(Currency.USD)
            )
    )


    private fun setRates(currency: Currency): BigDecimal {
        return when (currency) {
            Currency.EUR -> 0.12.toBigDecimal()

            Currency.DKK -> 1.00.toBigDecimal()

            Currency.GBP -> 0.13.toBigDecimal()

            Currency.SEK -> 1.46.toBigDecimal()

            Currency.USD -> 0.14.toBigDecimal()
        }
    }

}