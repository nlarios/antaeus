package io.pleo.antaeus.models

import java.sql.Timestamp

data class Billing (
    val id: Int,
    val customerId: Int,
    val totalAmount: Money,
    val timestamp: String
)