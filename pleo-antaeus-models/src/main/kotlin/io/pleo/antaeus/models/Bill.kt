package io.pleo.antaeus.models

data class Bill (
    val id: Int,
    val customerId: Int,
    val totalAmount: Int,
    var invoices: List<Invoice>
)