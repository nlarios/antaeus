package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.schedulers.BillingServiceScheduler
import io.pleo.antaeus.core.utils.convertCurrency
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.data.BillingDal
import io.pleo.antaeus.models.*
import java.sql.Timestamp
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.concurrent.RunnableScheduledFuture

class BillingService(
        private val paymentProvider: PaymentProvider,
        private val dal: BillingDal,
        private val customerService: CustomerService,
        private val invoiceService: InvoiceService
) {

    fun scheduleBillingForCustomer(id: Int, billingServiceScheduler: BillingServiceScheduler) {

        billingServiceScheduler.scheduleNextBillingTime { billCustomer(id, DateTimeFormatter.ISO_INSTANT.format(Instant.now())) }
    }

    fun billCustomer(id: Int, timestamp: String): Bill? {
        val customer = customerService.fetch(id)
        var invoices = invoiceService.fetchInvoicesByCustomerAndStatus(id, InvoiceStatus.PENDING)
        invoices = invoices.filter { invoice -> paymentProvider.charge(invoice = invoice, customer = customer) }
        var totalAmount = calculateSumOfInvoices(invoices)
        var bill = dal.createBill(customerId = customer.id, totalAmount = totalAmount, timestamp = timestamp)
        print(bill)
        return bill
    }



    fun calculateSumOfInvoices(invoices: List<Invoice>): Money {
        var totalAmount = 0.toBigDecimal()
        invoices.forEach { invoice ->
                totalAmount += convertCurrency(currencyFrom = invoice.amount.currency, amount = invoice.amount.value).value
        }
        return Money(totalAmount, currency = Currency.DKK)
    }
}