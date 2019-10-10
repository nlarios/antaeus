package io.pleo.antaeus.core.schedulers

import org.junit.jupiter.api.Test
import java.text.SimpleDateFormat
import java.util.*

class BillingServiceSchedulerTest {

    private val billingServiceScheduler = BillingServiceScheduler()

    @Test
    fun `check next first of month`() {
        val expectedDate =  SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").parse("2019-11-01 12:00:00:000")
        var date = billingServiceScheduler.calculateNextBillingDate()
        assert(expectedDate == date) {
            "Next billing date calculation failed"
        }
    }
}