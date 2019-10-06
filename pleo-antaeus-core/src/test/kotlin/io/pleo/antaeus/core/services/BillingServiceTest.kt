package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.core.exceptions.InvoiceNotFoundException
import io.pleo.antaeus.core.utils.BillingPaymentProvider
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.data.BillingDal
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BillingServiceTest {

    private val dal = mockk<BillingDal> {
        every { fetchInvoicesByCustomer(404) } returns emptyList();
    }

    private val paymentProvider = BillingPaymentProvider(dal = dal)

    private val billingService = BillingService(paymentProvider = paymentProvider, dal = dal)

    @Test
    fun `will throw if invoice list for costumer is empty`() {
        assertThrows<InvoiceNotFoundException> {
            billingService.fetchInvoicesByCustomer(404)
        }
    }

    @Test
    fun `will fail if billing of every costumer return nothing`(){
//        assert()
    }

    @Test
    fun `will fail if billing of any costumer fail`(){
        //assert()
    }

}