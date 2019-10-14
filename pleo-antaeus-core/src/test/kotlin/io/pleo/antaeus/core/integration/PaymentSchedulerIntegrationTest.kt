package io.pleo.antaeus.core.integration

import calculateTestDate
import io.pleo.antaeus.core.providers.BillingPaymentProvider
import io.pleo.antaeus.core.schedulers.PaymentScheduler
import io.pleo.antaeus.core.services.BillingService
import io.pleo.antaeus.core.services.CustomerService
import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.data.*
import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import setupInitialData
import java.sql.Connection
import java.text.SimpleDateFormat


class PaymentSchedulerIntegrationTest {


    private val paymentScheduler = PaymentScheduler()
    private val db:Database
    private val billingService: BillingService
    private val tables = arrayOf(InvoiceTable, CustomerTable, BillingTable)

    // Initialize database for integration test
    init {
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

        val customerDal = CustomerDal(db = db)
        val invoiceDal = InvoiceDal(db = db)
        val billingDal = BillingDal(db = db)
        // Insert example data in the database.
        setupInitialData(customerDal = customerDal, invoiceDal = invoiceDal)

        val invoiceService = InvoiceService(dal = invoiceDal)
        val customerService = CustomerService(dal = customerDal)
        val paymentProvider = BillingPaymentProvider(customerService, invoiceService )

        // This is _your_ billing service to be included where you see fit
        billingService = BillingService(paymentProvider = paymentProvider, dal = billingDal, customerService = customerService, invoiceService = invoiceService)

    }

    //Delete test db
    companion object {
        private val logger = KotlinLogging.logger {}

        @AfterAll
        fun cleanUpTest() {
            val tables = arrayOf(InvoiceTable, CustomerTable, BillingTable)
            Database
                    .connect("jdbc:sqlite:C:\\Users\\nikos\\docker\\volumes\\pleo-antaeus-sqlite\\data.db", "org.sqlite.JDBC").also {
                TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
                transaction(it) {
                    // Drop all existing tables to ensure a clean slate on each run
                    SchemaUtils.drop(*tables)
                    // Create all tables
                }
            }
        }
    }

    //  Main test for scheduled billing
    @Test
    fun `test scheduled payment (billing) for all customers`(){
        //schedule billing for the next second
        paymentScheduler.scheduleNextPayment({ billingService.billAllCustomers()}, calculateTestDate())
        //wait for a second to run payment service
        Thread.sleep(1_000)
        val billings = billingService.fetchAllBillings()

        assert(billings.isNotEmpty()) {
            "Scheduled payment service failed and didn't run"
        }

    }

    //Next payment date calculation test. It will fail if it runs after 2019-11-01 12:00:00:000. The expected date should change!
    @Test
    fun `check next first of month`() {
        val expectedDate =  SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").parse("2019-11-01 12:00:00:000")
        var date = paymentScheduler.calculateNextBillingDate()
        assert(expectedDate == date) {
            "Next billing date calculation failed. Is not 2019-11-01 12:00:00:000"
        }
    }



}