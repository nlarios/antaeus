package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.BillingNotFoundException
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.schedulers.BillingServiceScheduler
import io.pleo.antaeus.core.utils.convertCurrency
import io.pleo.antaeus.data.BillingDal
import io.pleo.antaeus.models.*
import java.time.Instant
import java.time.format.DateTimeFormatter
import mu.KotlinLogging

class BillingService(
        private val paymentProvider: PaymentProvider,
        private val dal: BillingDal,
        private val customerService: CustomerService,
        private val invoiceService: InvoiceService
) {

    private val logger = KotlinLogging.logger {}


    private fun returnBillCustomer(): ((Int, String) -> Billing?) {
        return ::billCustomer
    }

    private fun returnBillAllCustomer(): ((String) -> List<Billing?>?) {
        return ::billAllCustomers
    }

    fun scheduleBillingForCustomer(id: Int, billingServiceScheduler: BillingServiceScheduler) {
        billingServiceScheduler.scheduleNextBilling(returnBillCustomer(), id)
    }

    fun scheduleBillingForAllCustomers(billingServiceScheduler: BillingServiceScheduler) {
        billingServiceScheduler.scheduleNextBilling(returnBillAllCustomer())
    }

    fun billCustomer(id: Int, timestamp: String = DateTimeFormatter.ISO_INSTANT.format(Instant.now())): Billing? {
        val customer = customerService.fetch(id)
        var invoices = invoiceService.fetchInvoicesByCustomerAndStatus(id, InvoiceStatus.PENDING)

        invoices.forEach { invoice -> paymentProvider.charge(invoice = invoice, customer = customer) }
//        invoices.forEach { invoice -> invoiceService.payInvoice(invoice) }

        var totalAmount = calculateSumOfInvoices(invoices, customer)

        var billing = dal.createBilling(customerId = customer.id, totalAmount = totalAmount, timestamp = timestamp)
        logger.info("Billing of customer extracted: $billing")
        return billing
    }

    fun billAllCustomers(timestamp: String): List<Billing?> {
        val customers = customerService.fetchAll()
        var billings: List<Billing?> = mutableListOf()
        for (customer in customers) {
            billings += billCustomer(customer.id,timestamp)
        }
        return billings
    }

    fun calculateSumOfInvoices(invoices: List<Invoice>, customer: Customer): Money {
        var totalAmount = 0.toBigDecimal()
        invoices.forEach { invoice ->
            totalAmount += convertCurrency(currencyFrom = invoice.amount.currency, amount = invoice.amount.value).value
        }
        totalAmount = convertCurrency(currencyFrom = Currency.DKK, currencyTo = customer.balance.currency,amount = totalAmount).value
        return Money(totalAmount, currency = customer.balance.currency)
    }

    fun fetchBilling(id: Int): Billing {
        logger.info("Fetch billing with id: $id")

        return dal.fetchBill(id) ?: throw BillingNotFoundException(id)
    }


    fun fetchAllBillings(): List<Billing> {
        logger.info("")
        return dal.fetchAllBills()
    }


}