package io.pleo.antaeus.data

import io.pleo.antaeus.models.Bill
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Money
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

class BillingDal(private val db: Database) {

    fun fetchBill(id: Int): Bill? {
        return transaction(db) {
            BillTable
                    .select { BillTable.id.eq(id) }
                    .firstOrNull()
                    ?.toBill()
        }
    }

    fun fetchAllBills(): List<Bill> {
        return transaction(db) {
            InvoiceTable
                    .selectAll()
                    .map { it.toBill() }
        }
    }



    fun createBill(customerId: Int, totalAmount: Money, timestamp: String): Bill? {
        val id = transaction(db) {
            // Insert the bill and return its new id.
            BillTable.insert {
                it[this.customerId] = customerId
                it[this.value] = totalAmount.value
                it[this.currency] = totalAmount.currency.toString()
                it[this.timestamp] = timestamp
            } get BillTable.id
        }
        print(id)
        return fetchBill(id!!)
    }
}