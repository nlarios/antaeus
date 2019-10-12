package io.pleo.antaeus.core.providers

import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.services.CustomerService
import io.pleo.antaeus.core.utils.convertCurrency
import io.pleo.antaeus.models.Customer
import io.pleo.antaeus.models.Invoice

class BillingPaymentProvider(customerService: CustomerService): PaymentProvider {

    private val customerService = customerService


    override fun charge(invoice: Invoice, customer: Customer): Boolean {
        var customerCurrency = customer.balance.currency
        var invoiceAmount = convertCurrency(currencyFrom = invoice.amount.currency, amount = invoice.amount.value)
        var customerBalance = convertCurrency(currencyFrom = customer.balance.currency, amount = customer.balance.value)

        return if(invoiceAmount.value <= customerBalance.value) {
            customerBalance.value = customerBalance.value - invoiceAmount.value
            customer.balance = convertCurrency(currencyFrom = customerBalance.currency, currencyTo = customerCurrency, amount = customerBalance.value)
            customerService.updateCustomer(customer)
          println("last balance: ${customer.balance.value} invoice: ${invoice.amount.value}" )
            true
        }
        else {
            false
        }
    }

    fun chargeCustomer(invoice: Invoice, customer: Customer): Customer {
        var customerCurrency = customer.balance.currency
        var invoiceAmount = convertCurrency(currencyFrom = invoice.amount.currency, amount = invoice.amount.value)
        var customerBalance = convertCurrency(currencyFrom = customer.balance.currency, amount = customer.balance.value)
        customerBalance.value= customerBalance.value - invoiceAmount.value
        customer.balance = convertCurrency(currencyFrom = customer.balance.currency, currencyTo = customerCurrency, amount = customerBalance.value)

        return customer
    }
}