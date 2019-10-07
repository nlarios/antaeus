package io.pleo.antaeus.core.helpers

import io.pleo.antaeus.models.*
import java.math.BigDecimal

fun mockInvoice(
        id: Int = 1,
        customerId: Int = 1,
        amount: Money = mockMoney(),
        status: InvoiceStatus = InvoiceStatus.PAID
): Invoice {
    return Invoice(
            id = id,
            customerId = customerId,
            amount = amount,
            status = status
    )
}

fun mockCustomer(
        id: Int = 1,
        currency: Currency = Currency.EUR,
        balance: Money = mockMoney(10000.toBigDecimal())
): Customer {
    return Customer(
            id = id,
            currency = currency,
            balance = balance
    )
}

fun mockMoney(
        value: BigDecimal = 100.toBigDecimal(),
        currency: Currency = Currency.EUR
): Money {
    return Money(
            value = value,
            currency = currency
    )
}
