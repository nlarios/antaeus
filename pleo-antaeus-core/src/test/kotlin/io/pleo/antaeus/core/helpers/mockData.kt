package io.pleo.antaeus.core.helpers

import io.pleo.antaeus.models.*
import java.math.BigDecimal

// Helper methods for mocking data

fun mockInvoice(
        id: Int = 1,
        customerId: Int = 1,
        amount: Money = mockMoney(),
        status: InvoiceStatus = InvoiceStatus.PENDING
): Invoice {
    return Invoice(
            id = id,
            customerId = customerId,
            amount = amount,
            status = status
    )
}

fun mockCustomer(
        id: Int = 1,balance: Money = mockMoney(10000.toBigDecimal())
): Customer {
    return Customer(
            id = id,
            balance = balance
    )
}


fun mockBilling(
        id: Int = 1,
        customerId: Int = 1,
        totalAmount: Money = mockMoney(1000.toBigDecimal()),
        timestamp: String = "2018-12-12 01:02:03.123456789"
): Billing {
return Billing(
        id = id,
        customerId = customerId,
        totalAmount = totalAmount,
        timestamp = timestamp
)
}

fun mockMoney(
        value: BigDecimal = 1000.toBigDecimal(),
        currency: Currency = Currency.EUR
): Money {
    return Money(
            value = value,
            currency = currency
    )
}
