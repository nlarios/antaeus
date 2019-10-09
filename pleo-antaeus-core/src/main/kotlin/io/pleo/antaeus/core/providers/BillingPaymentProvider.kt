package io.pleo.antaeus.core.providers

import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.utils.convertCurrency
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.data.BillingDal
import io.pleo.antaeus.data.CustomerDal
import io.pleo.antaeus.data.InvoiceDal
import io.pleo.antaeus.models.Customer
import io.pleo.antaeus.models.Invoice

class BillingPaymentProvider(): PaymentProvider {



    override fun charge(invoice: Invoice, customer: Customer): Boolean {
        var invoiceAmount = convertCurrency(currencyFrom = invoice.amount.currency, amount = invoice.amount.value)
        var customerBalance = convertCurrency(currencyFrom = customer.balance.currency, amount = customer.balance.value)
        return invoiceAmount.value <= customerBalance.value

    }
}