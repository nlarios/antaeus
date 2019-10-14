package io.pleo.antaeus.core.schedulers

import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.core.helpers.mockBilling
import io.pleo.antaeus.core.services.BillingService
import org.junit.jupiter.api.Test
import java.text.SimpleDateFormat
import java.time.Clock
import java.util.*
import java.util.Calendar
import java.time.ZoneId
import java.time.Clock.fixed
import java.time.Instant


class BillingServiceSchedulerTest {

    private val billingServiceScheduler = BillingServiceScheduler()
    private val expectedBillings = listOf(mockBilling(id = 1), mockBilling(2))

    private val billingService = mockk<BillingService> {
        every{ billAllCustomers("2019-10-11T17:31:41.205513800Z") } returns expectedBillings
    }


    @Test
    fun `test scheduler`() {
        val calendar = Calendar.getInstance()
        val unroundedMinutes = calendar.get(Calendar.MINUTE)
        val mod = unroundedMinutes % 10
        calendar.add(Calendar.MINUTE, if (mod == 0) 10 else 10 - mod)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        billingServiceScheduler.scheduleNextBilling({ billingService.billAllCustomers("2019-10-11T17:31:41.205513800Z")}, calculateTestDate())

    }

    @Test
    fun `test scheduled billing`(){
        val instantExpected = "2014-12-22T10:15:30Z"
        val clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"))

        val instant = Instant.now(clock)

    }

    @Test
    fun `check next first of month`() {
        val expectedDate =  SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").parse("2019-11-01 12:00:00:000")
        var date = billingServiceScheduler.calculateNextBillingDate()
        assert(expectedDate == date) {
            "Next billing date calculation failed"
        }
    }

    fun calculateTestDate(): Date {
        val calendar = Calendar.getInstance()
        val unroundedMinutes = calendar.get(Calendar.MINUTE)
        // mod = unroundedMinutes % 10
        calendar.add(Calendar.MINUTE, 1)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
//        println(calendar.time)
        return calendar.time
    }

}