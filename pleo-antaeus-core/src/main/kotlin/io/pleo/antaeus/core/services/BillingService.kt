package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.utils.convertCurrency
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.data.BillingDal
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Money

class BillingService(
        private val paymentProvider: PaymentProvider,
        private val dal: BillingDal,
        private val customerService: CustomerService
) {


    fun billCustomer(id: Int) {
        var customer = customerService.fetch(id)
        var invoices = fetchInvoicesByCustomerAndStatus(id, InvoiceStatus.PENDING)
        invoices = invoices.filter { invoice -> paymentProvider.charge(invoice = invoice, customer = customer) }
        var totalAmount = calculateSumOfInvoices(invoices)

    }

    fun fetchInvoicesByCustomer(id: Int): List<Invoice> {
        return dal.fetchInvoicesByCustomer(id)
    }

    fun fetchInvoicesByCustomerAndStatus(id: Int, status: InvoiceStatus): List<Invoice> {
        return dal.fetchInvoicesByCostumerAndStatus(id, status)
    }


    fun calculateSumOfInvoices(invoices: List<Invoice>): Money {
        var totalAmount = 0.toBigDecimal()
        invoices.forEach { invoice ->
                totalAmount += convertCurrency(currencyFrom = invoice.amount.currency, amount = invoice.amount.value).value
        }
        return Money(totalAmount, currency = Currency.DKK)
    }
}