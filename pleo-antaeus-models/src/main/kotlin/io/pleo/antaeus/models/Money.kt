package io.pleo.antaeus.models

import java.math.BigDecimal

data class Money(
    var value: BigDecimal,
    val currency: Currency
)
