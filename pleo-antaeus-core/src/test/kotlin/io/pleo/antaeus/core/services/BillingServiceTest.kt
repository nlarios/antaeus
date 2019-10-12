package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.core.helpers.mockBill
import io.pleo.antaeus.core.helpers.mockCustomer
import io.pleo.antaeus.core.helpers.mockInvoice
import io.pleo.antaeus.core.helpers.mockMoney
import io.pleo.antaeus.core.providers.BillingPaymentProvider
import io.pleo.antaeus.data.BillingDal
import io.pleo.antaeus.data.CustomerDal
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Money
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class BillingServiceTest {

    private val expectedInvoices = listOf(mockInvoice(), mockInvoice(amount = mockMoney()))
    private val expectedCustomer = mockCustomer()
    private val expectedBill = mockBill()

    private val billingDal = mockk<BillingDal> {
        every { fetchInvoicesByCustomer(404) } returns emptyList();
        every { fetchInvoicesByCustomer(1) } returns expectedInvoices;
        every { createBill(1, Money(value = 240.toBigDecimal().setScale(2), currency = Currency.DKK), "2019-10-11T17:31:41.205513800Z") } returns expectedBill
    }

    private val customerDal = mockk<CustomerDal> {
        every { fetchCustomer(1) } returns expectedCustomer
    }

    private val invoiceService = mockk<InvoiceService> {
        every { fetchInvoicesByCustomerAndStatus(1, InvoiceStatus.PENDING) } returns expectedInvoices
    }


    private val customerService = CustomerService(customerDal)
    private val paymentProvider = BillingPaymentProvider(customerService)

    private val billingService = BillingService(paymentProvider = paymentProvider, dal = billingDal, customerService = customerService, invoiceService = invoiceService)


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
    fun `check billing of specific costumer`() {

        val actualBill = billingService.billCustomer(id = 1, timestamp = "2019-10-11T17:31:41.205513800Z")

        if (actualBill != null) {
            assert(expectedBill.id == actualBill.id && expectedBill.totalAmount == actualBill.totalAmount) {
                "Billing of specific customer by id failed"
            }
        } else
            assert(false) {
                "Billing is null"
            }
    }

}