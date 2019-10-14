package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.BillingNotFoundException
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.schedulers.PaymentScheduler
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
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    private fun returnBillCustomer(): ((Int, String) -> Billing?) {
        return ::billCustomer
    }

    private fun returnBillAllCustomer(): ((String) -> List<Billing?>?) {
        return ::billAllCustomers
    }

    //Initiate payment scheduler for billing one customer
    fun scheduleBillingForCustomer(id: Int, paymentScheduler: PaymentScheduler) {
        paymentScheduler.scheduleNextPayment(returnBillCustomer(), id)
    }

    //Initiate payment scheduler for billing all customers
    fun scheduleBillingForAllCustomers(paymentScheduler: PaymentScheduler) {
        paymentScheduler.scheduleNextPayment(returnBillAllCustomer())
    }

    //Main Method for billing a customer
    fun billCustomer(id: Int, timestamp: String = DateTimeFormatter.ISO_INSTANT.format(Instant.now())): Billing? {
        val customer = customerService.fetch(id)
        var invoices = invoiceService.fetchInvoicesByCustomerAndStatus(id, InvoiceStatus.PENDING)

        //if an invoice is failed to charged is not added in th calculation
        invoices = invoices.filter { invoice -> paymentProvider.charge(invoice = invoice, customer = customer) }

        var totalAmount = calculateSumOfInvoices(invoices, customer)

        var billing = dal.createBilling(customerId = customer.id, totalAmount = totalAmount, timestamp = timestamp)
        logger.info("Billing of customer extracted: $billing")
        return billing
    }

    fun billAllCustomers(timestamp: String = DateTimeFormatter.ISO_INSTANT.format(Instant.now())): List<Billing?> {
        val customers = customerService.fetchAll()
        var billings: List<Billing?> = mutableListOf()
        for (customer in customers) {
            billings += billCustomer(customer.id,timestamp)
        }
        return billings
    }

    //Helper method for calculating the sum of multiple invoices
    fun calculateSumOfInvoices(invoices: List<Invoice>, customer: Customer): Money {
        var totalAmount = 0.toBigDecimal()
        //change all invoices amount into base currency
        invoices.forEach { invoice ->
            totalAmount += convertCurrency(currencyFrom = invoice.amount.currency, amount = invoice.amount.value).value
        }
        //change back amount to total currency
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