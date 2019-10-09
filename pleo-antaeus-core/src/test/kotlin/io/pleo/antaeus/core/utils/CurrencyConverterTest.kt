package io.pleo.antaeus.core.utils

import io.pleo.antaeus.core.helpers.mockMoney
import io.pleo.antaeus.models.Currency
import org.junit.jupiter.api.Test

class CurrencyConverterTest {

    val currencyConverter = CurrencyConverter()

    @Test
    fun `check currency conversion`() {
        var actualMoney = mockMoney(13456.toBigDecimal(), Currency.SEK)
        var expectedMoney = mockMoney(19645.76.toBigDecimal(), Currency.DKK)
        actualMoney = currencyConverter.convertCurrency(Currency.SEK,Currency.DKK, actualMoney.value)
        assert(actualMoney == expectedMoney) {
            "If fail currency converter to DDK don't work"
        }
    }

}