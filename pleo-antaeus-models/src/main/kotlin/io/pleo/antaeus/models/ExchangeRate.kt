package io.pleo.antaeus.models

import java.math.BigDecimal

data class ExchangeRate(
        val base: Currency,
        var rates: Map<Currency, BigDecimal>
)
