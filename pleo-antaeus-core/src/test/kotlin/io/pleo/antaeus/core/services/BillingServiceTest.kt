package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.core.helpers.*
import io.pleo.antaeus.core.providers.BillingPaymentProvider
import io.pleo.antaeus.data.BillingDal
import io.pleo.antaeus.data.CustomerDal
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Money
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.ZoneId
import java.time.Clock.fixed
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime


class BillingServiceTest {

    // Expected values for test
    private val expectedInvoices = listOf(mockInvoice(), mockInvoice(amount = mockMoney()))
    private val expectedCustomer = mockCustomer()
    private val expectedBilling = mockBilling(id = 1,totalAmount = mockMoney(value = 1999.92.toBigDecimal().setScale(2)))
    private val expectedBillings = listOf(mockBilling(id = 1,totalAmount = mockMoney(value = 1999.92.toBigDecimal().setScale(2))), mockBilling(2,totalAmount = mockMoney(value = 1999.92.toBigDecimal().setScale(2))),  mockBilling(2, totalAmount = mockMoney(value = 1999.92.toBigDecimal().setScale(2))))


    // Mock Data
    private val billingDal = mockk<BillingDal> {
        every { createBilling(1, Money(value = 240.toBigDecimal().setScale(2), currency = Currency.DKK), "2019-10-11T17:31:41.205513800Z") } returns expectedBilling
        every { createBilling(1, Money(value = 1999.92.toBigDecimal().setScale(2), currency = Currency.EUR), "2019-10-11T17:31:41.205513800Z") } returns mockBilling(1, totalAmount = mockMoney(value = 1999.92.toBigDecimal().setScale(2), currency = Currency.EUR))
        every { createBilling(2, Money(value = 1999.92.toBigDecimal().setScale(2), currency = Currency.EUR), "2019-10-11T17:31:41.205513800Z") } returns mockBilling(2, totalAmount = mockMoney(value = 1999.92.toBigDecimal().setScale(2), currency = Currency.EUR))

    }

    private val customerService = mockk<CustomerService> {
        every { fetch(1) } returns expectedCustomer
        every { fetch(2) } returns mockCustomer(2)
        every { fetch(3) } returns mockCustomer(2)
        every { fetchAll() } returns listOf(mockCustomer(id = 1), mockCustomer(2), mockCustomer(3))
        every { updateCustomer(mockCustomer(id = 1, balance = Money(value = 9000.toBigDecimal(), currency = Currency.EUR))) } returns Unit
        every { updateCustomer(mockCustomer(id = 2, balance = Money(value = 9000.toBigDecimal(), currency = Currency.EUR))) } returns Unit
        every { updateCustomer(mockCustomer(id = 3, balance = Money(value = 9000.toBigDecimal(), currency = Currency.EUR))) } returns Unit
        every { updateCustomer(mockCustomer(id = 1, balance = Money(value = 8000.toBigDecimal(), currency = Currency.EUR))) } returns Unit
        every { updateCustomer(mockCustomer(id = 2, balance = Money(value = 8000.toBigDecimal(), currency = Currency.EUR))) } returns Unit

    }

    private val invoiceService = mockk<InvoiceService> {
        every { fetchInvoicesByCustomer(404) } returns emptyList();
        every { fetchInvoicesByCustomer(1) } returns expectedInvoices;
        every {fetchInvoicesByCustomerAndStatus(2, InvoiceStatus.PENDING)} returns listOf(mockInvoice(3, customerId = 2), mockInvoice(4,customerId =2))
        every { fetchInvoicesByCustomerAndStatus(1, InvoiceStatus.PENDING) } returns expectedInvoices
        every { fetchInvoicesByCustomerAndStatus(3, InvoiceStatus.PENDING) } returns expectedInvoices

        every { payInvoice(Invoice(id = 1, customerId = 1, amount = Money(value = 1000.toBigDecimal(), currency = Currency.EUR), status = InvoiceStatus.PENDING)) } returns mockInvoice(id = 1, customerId = 1, amount = Money(value = 1000.toBigDecimal(), currency = Currency.EUR), status = InvoiceStatus.PAID)
        every { payInvoice(Invoice(id = 3, customerId = 2, amount = Money(value = 1000.toBigDecimal(), currency = Currency.EUR), status = InvoiceStatus.PENDING)) } returns mockInvoice(id = 3, customerId = 2, amount = Money(value = 1000.toBigDecimal(), currency = Currency.EUR), status = InvoiceStatus.PAID)
        every { payInvoice(Invoice(id = 4, customerId = 2, amount = Money(value = 1000.toBigDecimal(), currency = Currency.EUR), status = InvoiceStatus.PENDING)) } returns mockInvoice(id = 4, customerId = 2, amount = Money(value = 1000.toBigDecimal(), currency = Currency.EUR), status = InvoiceStatus.PAID)

    }

    private val paymentProvider = BillingPaymentProvider(customerService, invoiceService)

    private val billingService = BillingService(paymentProvider = paymentProvider, dal = billingDal, customerService = customerService, invoiceService = invoiceService)


    @Test
    fun `calculate sum of invoices for billing of a customer`() {
        var invoices = listOf(mockInvoice())
        val expectedBillingAmount = mockMoney((999.96).toBigDecimal())
        val customer = mockCustomer()
        assert(billingService.calculateSumOfInvoices(invoices, customer) == expectedBillingAmount) {
            "Calculation of total billing for a customer failed"
        }
    }

    @Test
    fun `check billing for every costumer`() {
        val instantExpected ="2019-10-11T17:31:41.205513800Z"
        val clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"))
//        val instant = Instant.now(clock)

        val actualBillings = billingService.billAllCustomers(instantExpected)
        assert(actualBillings == expectedBillings) {
            "Calculation of all billings for all customers failed"
        }
    }

    @Test
    fun `check billing of specific costumer`() {
        val actualBilling = billingService.billCustomer(id = 1, timestamp = "2019-10-11T17:31:41.205513800Z")

        if (actualBilling != null) {
            assert(expectedBilling.id == actualBilling.id && expectedBilling.totalAmount == actualBilling.totalAmount) {
                "Billing of specific customer by id failed"
            }
        } else
            assert(false) {
                "Billing is null"
            }
    }

}