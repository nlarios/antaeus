package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.BillNotFoundException
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.schedulers.BillingServiceScheduler
import io.pleo.antaeus.core.utils.convertCurrency
import io.pleo.antaeus.data.BillingDal
import io.pleo.antaeus.models.*
import java.time.Instant
import java.time.format.DateTimeFormatter

class BillingService(
        private val paymentProvider: PaymentProvider,
        private val dal: BillingDal,
        private val customerService: CustomerService,
        private val invoiceService: InvoiceService
) {

    private fun returnBillCustomer(): ((Int, String) -> Bill?) {
        return ::billCustomer
    }

    private fun returnBillAllCustomer(): (() -> Unit?) {
        return ::billAllCustomers
    }

    fun scheduleBillingForCustomer(id: Int, billingServiceScheduler: BillingServiceScheduler) {
        billingServiceScheduler.scheduleNextBillingTime(returnBillCustomer(), id)
    }

    fun scheduleBillingForAllCustomers(billingServiceScheduler: BillingServiceScheduler) {
        billingServiceScheduler.scheduleNextBillingTime(returnBillAllCustomer(), billingServiceScheduler.calculateTestDate())
    }

    fun billCustomer(id: Int, timestamp: String = DateTimeFormatter.ISO_INSTANT.format(Instant.now())): Bill? {
        val customer = customerService.fetch(id)
        var invoices = invoiceService.fetchInvoicesByCustomerAndStatus(id, InvoiceStatus.PENDING)
        println("Im inside billing of customer")
        invoices = invoices.filter { invoice -> paymentProvider.charge(invoice = invoice, customer = customer) }
        invoices.forEach { invoice -> invoiceService.payInvoice(invoice) }

        var totalAmount = calculateSumOfInvoices(invoices)

        var bill = dal.createBill(customerId = customer.id, totalAmount = totalAmount, timestamp = timestamp)
        println(bill)
        return bill
    }

    fun billAllCustomers() {
        println("Im in bill ")
        val customers = customerService.fetchAll()
        customers.forEach { customer -> billCustomer(customer.id) }
    }

    fun calculateSumOfInvoices(invoices: List<Invoice>): Money {
        var totalAmount = 0.toBigDecimal()
        invoices.forEach { invoice ->
            totalAmount += convertCurrency(currencyFrom = invoice.amount.currency, amount = invoice.amount.value).value
        }
        return Money(totalAmount, currency = Currency.DKK)
    }

    fun fetchBill(id: Int): Bill {
        return dal.fetchBill(id) ?: throw BillNotFoundException(id)
    }


    fun fetchAllBills(): List<Bill> {
        return dal.fetchAllBills()
    }


}