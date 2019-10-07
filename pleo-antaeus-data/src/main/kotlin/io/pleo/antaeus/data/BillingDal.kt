package io.pleo.antaeus.data

import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class BillingDal(private val db: Database) {
    fun fetchInvoicesByCustomer(id: Int): List<Invoice> {
        return transaction(db) {
            InvoiceTable
                    .select { InvoiceTable.customerId.eq(id) }
                    .map { it.toInvoice() }
        }
    }

    fun fetchInvoicesByCostumerAndStatus(id: Int, status: InvoiceStatus): List<Invoice> {
        return transaction(db) {
            InvoiceTable
                    .select { InvoiceTable.customerId.eq(id) and InvoiceTable.status.eq(status.toString())}
                    .map { it.toInvoice() }
        }
    }
}