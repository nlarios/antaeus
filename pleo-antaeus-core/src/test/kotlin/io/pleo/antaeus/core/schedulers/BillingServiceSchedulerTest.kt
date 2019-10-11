package io.pleo.antaeus.core.schedulers

import io.mockk.mockk
import io.pleo.antaeus.core.services.BillingService
import org.junit.jupiter.api.Test
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar



class BillingServiceSchedulerTest {

    private val billingServiceScheduler = BillingServiceScheduler()
    private val billingService = mockk<BillingService> {

    }


    @Test
    fun `test scheduler`() {
        val calendar = Calendar.getInstance()
        val unroundedMinutes = calendar.get(Calendar.MINUTE)
        val mod = unroundedMinutes % 10
        calendar.add(Calendar.MINUTE, if (mod == 0) 10 else 10 - mod)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        billingServiceScheduler.scheduleNextBillingTime({ print("I m the scheduled task")}, calendar.time)

    }

    @Test
    fun `check next first of month`() {
        val expectedDate =  SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").parse("2019-11-01 12:00:00:000")
        var date = billingServiceScheduler.calculateNextBillingDate()
        assert(expectedDate == date) {
            "Next billing date calculation failed"
        }
    }
}