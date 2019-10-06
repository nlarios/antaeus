package io.pleo.antaeus.core.utils

import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.data.BillingDal
import io.pleo.antaeus.data.InvoiceDal
import io.pleo.antaeus.models.Invoice

class BillingPaymentProvider(dal: BillingDal): PaymentProvider {
    override fun charge(invoice: Invoice): Boolean {

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}