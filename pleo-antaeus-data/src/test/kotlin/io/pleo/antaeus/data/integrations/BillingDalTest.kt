package io.pleo.antaeus.data.integrations

import io.pleo.antaeus.data.BillTable
import io.pleo.antaeus.data.BillingDal
import io.pleo.antaeus.data.CustomerTable
import io.pleo.antaeus.data.InvoiceTable
import io.pleo.antaeus.models.Bill
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Money
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test
import org.sqlite.core.DB
import java.sql.Connection

class BillingDalIntegrationTest {

    private val db:Database
    private val expectedBill = Bill(1, 1, Money(value=240.00.toBigDecimal(), currency= Currency.DKK),"2019-10-11T17:31:41.205513800Z")

    init {
        val tables = arrayOf(InvoiceTable, CustomerTable, BillTable)

        this.db = Database
                .connect("jdbc:sqlite:C:\\Users\\nikos\\docker\\volumes\\pleo-antaeus-sqlite\\data.db", "org.sqlite.JDBC")
                .also {
                    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
                    transaction(it) {
                        addLogger(StdOutSqlLogger)
                        // Drop all existing tables to ensure a clean slate on each run
                        SchemaUtils.drop(*tables)
                        // Create all tables
                        SchemaUtils.create(*tables)
                    }
                }
    }

    private val billingDal = BillingDal(db =db )

    @Test
    fun `create bill`() {
        val bill = billingDal.createBill(1, Money(value=240.00.toBigDecimal(), currency= Currency.DKK),"2019-10-11T17:31:41.205513800Z")
        print(bill)
        assert(bill == expectedBill) {
            "Bill creation failed"
        }
    }
}