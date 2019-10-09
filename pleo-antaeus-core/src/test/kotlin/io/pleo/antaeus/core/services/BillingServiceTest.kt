package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.core.helpers.mockInvoice
import io.pleo.antaeus.core.helpers.mockMoney
import io.pleo.antaeus.core.providers.BillingPaymentProvider
import io.pleo.antaeus.data.BillingDal
import io.pleo.antaeus.data.CustomerDal
import io.pleo.antaeus.models.Invoice
import org.junit.jupiter.api.Test

class BillingServiceTest {

    private val expectedInvoices = listOf<Invoice>(mockInvoice())

    private val billingDal = mockk<BillingDal> {
        every { fetchInvoicesByCustomer(404) } returns emptyList();
        every { fetchInvoicesByCustomer(1) } returns expectedInvoices;
    }

    private val customerDal = mockk<CustomerDal> {

    }

    private val paymentProvider = BillingPaymentProvider()

    private val customerService = CustomerService(customerDal)

    private val billingService = BillingService(paymentProvider = paymentProvider, dal = billingDal, customerService = customerService)

    @Test
    fun `will throw if invoice list for costumer is empty`() {

        assert(billingService.fetchInvoicesByCustomer(404).isEmpty()) { "Invoice by Costumer with id 404 did not found" }
    }

    @Test
    fun `get all invoices of users`() {
        val actualInvoices = billingService.fetchInvoicesByCustomer(1)
        assert(expectedInvoices == actualInvoices) {
            "Fetching Invoices of specific Customer failed"
        }
    }

    @Test
    fun `calculate billing of a customer`() {
        var invoices = listOf(mockInvoice())
        val expectedBillingAmount = mockMoney((10000).toBigDecimal())

        assert(billingService.calculateSumOfInvoices(invoices).equals(expectedBillingAmount)) {
            "Calculate total billing of a customer failed"
        }
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