package io.pleo.pleo.antaeus.rest.integration.test

import io.pleo.antaeus.core.providers.BillingPaymentProvider
import io.pleo.antaeus.core.schedulers.BillingServiceScheduler
import io.pleo.antaeus.core.services.BillingService
import io.pleo.antaeus.core.services.CustomerService
import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.data.*
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.rest.AntaeusRest
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import setupInitialData
import java.sql.Connection

class AntaeusRestIntegrationTest {

    private val db:Database
    private val billingServiceScheduler = BillingServiceScheduler()
    private val expectedInvoices: List<Invoice>
    private val billingService: BillingService

   init {

        val tables = arrayOf(InvoiceTable, CustomerTable, BillingTable)

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



       // Create core services
       val invoiceService = InvoiceService(dal = invoiceDal)
       val customerService = CustomerService(dal = customerDal)
       expectedInvoices = invoiceService.fetchAll()

       // Get third parties
       val paymentProvider = BillingPaymentProvider(customerService, invoiceService )

       // This is _your_ billing service to be included where you see fit
       billingService = BillingService(paymentProvider = paymentProvider, dal = billingDal, customerService = customerService, invoiceService = invoiceService)

       val scheduler = BillingServiceScheduler()

       // Create REST web service
       AntaeusRest(
               invoiceService = invoiceService,
               customerService = customerService,
               billingService = billingService,
               scheduler = scheduler
       ).run()
    }

    @Test
    fun `check REST api get`(){

    }


    @Test
    fun `check that scheduler fills billing database`() {
        val mockedDate = billingServiceScheduler.calculateNextBillingDate()

        val billings = billingService.fetchAllBillings()

        assert(billings.isNotEmpty()) {
            "Billing failed, none was created"
        }

    }


}