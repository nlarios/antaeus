/*
    Defines mappings between database rows and Kotlin objects.
    To be used by `AntaeusDal`.
 */

package io.pleo.antaeus.data

import io.pleo.antaeus.models.*
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toInvoice(): Invoice = Invoice(
        id = this[InvoiceTable.id],
        amount = Money(
                value = this[InvoiceTable.value],
                currency = Currency.valueOf(this[InvoiceTable.currency])
        ),
        status = InvoiceStatus.valueOf(this[InvoiceTable.status]),
        customerId = this[InvoiceTable.customerId]
)

fun ResultRow.toCustomer(): Customer = Customer(
        id = this[CustomerTable.id],
        balance = Money(
                value = this[CustomerTable.balance],
                currency = Currency.valueOf(this[CustomerTable.currency])
        )
)

fun ResultRow.toBilling(): Billing = Billing(
        id = this[BillingTable.id],
        customerId = this[BillingTable.customerId],
        totalAmount = Money(
                value = this[BillingTable.value],
                currency = Currency.valueOf(this[BillingTable.currency])
        ),
        timestamp = this[BillingTable.timestamp]
)


