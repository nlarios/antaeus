/*
    Implements endpoints related to invoices.
 */

package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.InvoiceNotFoundException
import io.pleo.antaeus.data.InvoiceDal
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus

class InvoiceService(private val dal: InvoiceDal) {
    fun fetchAll(): List<Invoice> {
       return dal.fetchInvoices()
    }

    fun fetch(id: Int): Invoice {
        return dal.fetchInvoice(id) ?: throw InvoiceNotFoundException(id)
    }

    fun fetchInvoicesByCustomer(id: Int): List<Invoice> {
        return dal.fetchInvoicesByCustomer(id)
    }

    fun fetchInvoicesByCustomerAndStatus(id: Int, status: InvoiceStatus): List<Invoice> {
        return dal.fetchInvoicesByCostumerAndStatus(id, status)
    }

    //not just update, room for more business logic
    fun payInvoice(invoice: Invoice): Invoice? {
        invoice.status = InvoiceStatus.PAID
        return dal.updateInvoice(invoice)
    }

}
