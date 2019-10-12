package io.pleo.antaeus.core.utils

import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.helpers.mockCustomer
import io.pleo.antaeus.core.helpers.mockInvoice
import io.pleo.antaeus.core.providers.BillingPaymentProvider
import io.pleo.antaeus.core.services.CustomerService
import org.junit.jupiter.api.Test

class BillingPaymentProviderTest {
    private val customer = mockCustomer()
    private val invoice = mockInvoice()

    private val customerService = mockk<CustomerService> {
//        every {  }
    }

    val paymentProvider = BillingPaymentProvider(customerService)

    @Test
    fun `check paymentProvide`(){
        var before = customer.balance.value
        println("customer balance before: ${customer.balance.value}")

        paymentProvider.charge(invoice, customer)
        println("customer balance after: ${customer.balance.value}")
        assert(customer.balance.value < before) {
            "Customer didn't charged"
        }
    }
}