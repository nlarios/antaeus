package io.pleo.antaeus.data

import io.pleo.antaeus.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class CustomerDal(private val db: Database) {
    fun fetchCustomer(id: Int): Customer? {
        return transaction(db) {
            CustomerTable
                .select { CustomerTable.id.eq(id) }
                .firstOrNull()
                ?.toCustomer()
        }
    }

    fun fetchCustomers(): List<Customer> {
        return transaction(db) {
            CustomerTable
                .selectAll()
                .map { it.toCustomer() }
        }
    }

    fun createCustomer(balance: Money): Customer? {
        val id = transaction(db) {
            // Insert the customer and return its new id.
            CustomerTable.insert {
                it[this.balance] = balance.value
                it[this.currency] = balance.currency.toString()
            } get CustomerTable.id
        }

        return fetchCustomer(id!!)
    }

    fun updateCustomer(customer: Customer) {
        transaction(db) {
            CustomerTable
                    .update({ CustomerTable.id eq customer.id }) {
                        it[this.balance] = customer.balance.value
                        it[this.currency] = customer.balance.currency.toString()
                    }
        }
    }
}