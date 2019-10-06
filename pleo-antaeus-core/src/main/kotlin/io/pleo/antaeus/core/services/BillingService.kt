package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.data.BillingDal
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus

class BillingService(
    private val paymentProvider: PaymentProvider,
    private val dal: BillingDal
) {

    fun fetchInvoicesByCustomer(id: Int): List<Invoice>{

        return dal.fetchInvoicesByCustomer(id)
    }

    fun fetchInvoicesByCustomerAndStatus(id: Int, status: InvoiceStatus) {

    }

}