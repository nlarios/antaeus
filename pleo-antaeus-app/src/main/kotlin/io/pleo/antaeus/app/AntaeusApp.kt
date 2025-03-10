/*
    Defines the main() entry point of the app.
    Configures the database and sets up the REST web service.
 */

@file:JvmName("AntaeusApp")

package io.pleo.antaeus.app

import getPaymentProvider
import io.pleo.antaeus.core.schedulers.PaymentScheduler
import io.pleo.antaeus.core.services.BillingService
import io.pleo.antaeus.core.services.CustomerService
import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.data.*
import io.pleo.antaeus.rest.AntaeusRest
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import setupInitialData
import java.sql.Connection

fun main() {
    // The tables to create in the database.
    val tables = arrayOf(InvoiceTable, CustomerTable, BillingTable)
    //path for local builds: jdbc:sqlite:C:\\Users\\nikos\\docker\\volumes\\pleo-antaeus-sqlite\\data.db

    // Connect to the database and create the needed tables. Drop any existing data.
    val db = Database
            .connect("jdbc:sqlite:/tmp/data.db", "org.sqlite.JDBC")
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

    // Set up data access layer.
    val customerDal = CustomerDal(db = db)
    val invoiceDal = InvoiceDal(db = db)
    val billingDal = BillingDal(db = db)
    // Insert example data in the database.
    setupInitialData(customerDal = customerDal, invoiceDal = invoiceDal)



    // Create core services
    val invoiceService = InvoiceService(dal = invoiceDal)
    val customerService = CustomerService(dal = customerDal)

    // Get third parties
    val paymentProvider = getPaymentProvider(true, customerService, invoiceService )

    // This is _your_ billing service to be included where you see fit
    val billingService = BillingService(paymentProvider = paymentProvider, dal = billingDal, customerService = customerService, invoiceService = invoiceService)

    //instantiation of scheduler
    val scheduler = PaymentScheduler()

    //initiate billing of all customers
    billingService.scheduleBillingForAllCustomers(scheduler)

    // Create REST web service
    AntaeusRest(
            invoiceService = invoiceService,
            customerService = customerService,
            billingService = billingService,
            scheduler = scheduler
    ).run()
}

