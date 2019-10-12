package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.core.exceptions.InvoiceNotFoundException
import io.pleo.antaeus.core.helpers.mockInvoice
import io.pleo.antaeus.data.InvoiceDal
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class InvoiceServiceTest {
    private val expectedInvoices = listOf(mockInvoice())


    private val dal = mockk<InvoiceDal> {
        every { fetchInvoice(404) } returns null
    }

    private val invoiceService = InvoiceService(dal = dal)

    @Test
    fun `will throw if invoice is not found`() {
        assertThrows<InvoiceNotFoundException> {
            invoiceService.fetch(404)
        }
    }

    @Test
    fun `will throw if invoice list for costumer is empty`() {

        assert(invoiceService.fetchInvoicesByCustomer(404).isEmpty()) { "Invoice by Costumer with id 404 did not found" }
    }

    @Test
    fun `get all invoices of users`() {
        val actualInvoices = invoiceService.fetchInvoicesByCustomer(1)
        assert(expectedInvoices == actualInvoices) {
            "Fetching Invoices of specific Customer failed"
        }
    }
}
