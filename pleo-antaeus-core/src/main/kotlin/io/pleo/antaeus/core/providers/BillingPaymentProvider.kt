package io.pleo.antaeus.core.providers

import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.services.CustomerService
import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.core.utils.convertCurrency
import io.pleo.antaeus.models.Customer
import io.pleo.antaeus.models.Invoice
import mu.KotlinLogging

class BillingPaymentProvider(
        customerService: CustomerService,
        invoiceService: InvoiceService
        ): PaymentProvider {

    private val customerService = customerService
    private val invoiceService = invoiceService

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    override fun charge(invoice: Invoice, customer: Customer): Boolean {
        var customerCurrency = customer.balance.currency
        var invoiceAmount = invoice.amount
        var customerBalance = customer.balance
        if(invoiceAmount.currency != customerBalance.currency) {
            invoiceAmount = convertCurrency(currencyFrom = invoice.amount.currency, amount = invoice.amount.value)
            customerBalance = convertCurrency(currencyFrom = customer.balance.currency, amount = customer.balance.value)
        }

        return if(invoiceAmount.value <= customerBalance.value) {
            customerBalance.value = customerBalance.value - invoiceAmount.value
            if(invoiceAmount.currency != customerBalance.currency)
                customer.balance = convertCurrency(currencyFrom = customerBalance.currency, currencyTo = customerCurrency, amount = customerBalance.value)
            customerService.updateCustomer(customer)
            invoiceService.payInvoice(invoice)
            logger.info("Invoice ${invoice.id} charged successfully with invoice amount: ${invoice.amount.value} and customers balance is: ${customer.balance.value}")
            true
        }
        else {
            logger.info("Invoice ${invoice.id} didn't charged with invoice amount: ${invoice.amount.value} and customers balance is: ${customer.balance.value}")
            false
        }
    }

}