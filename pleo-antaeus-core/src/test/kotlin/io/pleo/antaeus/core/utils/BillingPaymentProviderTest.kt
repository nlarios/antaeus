package io.pleo.antaeus.core.utils

import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.helpers.mockCustomer
import io.pleo.antaeus.core.helpers.mockInvoice
import io.pleo.antaeus.core.providers.BillingPaymentProvider
import io.pleo.antaeus.core.services.CustomerService
import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import org.junit.jupiter.api.Test

class BillingPaymentProviderTest {
    private val customer = mockCustomer()
    private val invoice = mockInvoice()

    private val customerService = mockk<CustomerService> {
        every { updateCustomer(customer) }
    }

    private val invoiceService = mockk<InvoiceService> {
        every { payInvoice(invoice) } returns mockInvoice(status = InvoiceStatus.PAID)
    }

    private val paymentProvider = BillingPaymentProvider(customerService, invoiceService)

    @Test
    fun `check paymentProvider charging`(){
        var before = customer.balance.value
        paymentProvider.charge(invoice, customer)
        assert(customer.balance.value < before) {
            "Customer didn't charged"
        }
    }
}