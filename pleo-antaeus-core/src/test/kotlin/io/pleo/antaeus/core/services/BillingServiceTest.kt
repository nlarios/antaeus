package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.core.exceptions.InvoiceNotFoundException
import io.pleo.antaeus.core.helpers.mockInvoice
import io.pleo.antaeus.core.utils.BillingPaymentProvider
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.data.BillingDal
import io.pleo.antaeus.data.CustomerDal
import io.pleo.antaeus.models.Bill
import io.pleo.antaeus.models.Invoice
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows

class BillingServiceTest {

    private val expectedInvoices = listOf<Invoice>(mockInvoice())

    private val billingDal = mockk<BillingDal> {
        every { fetchInvoicesByCustomer(404) } returns emptyList();
        every { fetchInvoicesByCustomer(1) } returns expectedInvoices;
    }

    private val customerDal = mockk<CustomerDal> {

    }

    private val paymentProvider = BillingPaymentProvider(dal = billingDal)

    private val billingService = BillingService(paymentProvider = paymentProvider, dal = billingDal)

    @Test
    fun `will throw if invoice list for costumer is empty`() {
        assertThrows<InvoiceNotFoundException> {
            billingService.fetchInvoicesByCustomer(404)
        }
    }

    @Test
    fun `get all invoices of users`() {
        val actualInvoices = billingService.fetchInvoicesByCustomer(1)
        assert(expectedInvoices == actualInvoices){
            "Fetching Invoices of specific Customer"
        }
    }


    @Test
    fun `calculate billing of a customer`() {
//        Bill bill = billingService.
        assert(false)
    }

    @Test
    fun `will fail if billing of every costumer return nothing`() {
        assert(false)
    }

    @Test
    fun `will fail if billing of any costumer fail`() {
        assert(false)
    }

}