import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.data.CustomerDal
import io.pleo.antaeus.data.InvoiceDal
import io.pleo.antaeus.models.*
import java.math.BigDecimal
import kotlin.random.Random

// This will create all schemas and setup initial data
internal fun setupInitialData(customerDal: CustomerDal, invoiceDal: InvoiceDal) {
    val customers = (1..100).mapNotNull {
        customerDal.createCustomer(
                balance = Money(
                        value = BigDecimal(Random.nextDouble(1000.0, 50000.0)),
                        currency = Currency.values()[Random.nextInt(0, Currency.values().size)]
                )
        )
    }

    customers.forEach { customer ->
        (1..10).forEach {
            invoiceDal.createInvoice(
                    amount = Money(
                            value = BigDecimal(Random.nextDouble(10.0, 500.0)),
                            currency = customer.balance.currency
                    ),
                    customer = customer,
                    status = if (it == 1) InvoiceStatus.PENDING else InvoiceStatus.PAID
            )
        }
    }
}

// This is the mocked instance of the payment provider
internal fun getPaymentProvider(): PaymentProvider {
    return object : PaymentProvider {
        override fun charge(invoice: Invoice, customer: Customer): Boolean {
            return Random.nextBoolean()
        }
    }
}