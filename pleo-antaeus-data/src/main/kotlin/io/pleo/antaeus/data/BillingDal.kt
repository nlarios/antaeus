package io.pleo.antaeus.data

import io.pleo.antaeus.models.Billing
import io.pleo.antaeus.models.Money
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class BillingDal(private val db: Database) {

    fun fetchBill(id: Int): Billing? {
        return transaction(db) {
            BillingTable
                    .select { BillingTable.id.eq(id) }
                    .firstOrNull()
                    ?.toBilling()
        }
    }

    fun fetchAllBills(): List<Billing> {
        return transaction(db) {
            BillingTable
                    .selectAll()
                    .map { it.toBilling() }
        }
    }



    fun createBilling(customerId: Int, totalAmount: Money, timestamp: String): Billing? {
        val id = transaction(db) {
            // Insert the bill and return its new id.
            BillingTable.insert {
                it[this.customerId] = customerId
                it[this.value] = totalAmount.value
                it[this.currency] = totalAmount.currency.toString()
                it[this.timestamp] = timestamp
            } get BillingTable.id
        }
        print(id)
        return fetchBill(id!!)
    }
}