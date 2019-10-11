package io.pleo.antaeus.core.schedulers

import io.pleo.antaeus.models.Bill
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.schedule
import java.util.Calendar


class BillingServiceScheduler() {


    fun scheduleNextBillingTime(billingAction: ((Int, timestamp: String) -> Bill?), id: Int, date: Date = calculateNextBillingDate()) {
//        var nextTime = calculateNextBillingDate()
        var date = calculateTestDate()
        Timer("SettingUpBillingSchedule", false).schedule(time = date) {
            billingAction(id, DateTimeFormatter.ISO_INSTANT.format(Instant.now()))
            scheduleNextBillingTime(billingAction, id)
        }
    }

    fun calculateNextBillingDate(): Date {
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDateString = sdf.format(Date())
        val currentDate = Date()
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, 1)
        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR, calendar.getActualMinimum(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE))
        calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND))
        calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND))

        return calendar.time
    }

    fun calculateTestDate(): Date {
        val calendar = Calendar.getInstance()
        val unroundedMinutes = calendar.get(Calendar.MINUTE)
        // mod = unroundedMinutes % 10
        calendar.add(Calendar.MINUTE, 1)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        println(calendar.time)
        return calendar.time
    }


}
//        CoroutineScope {
//    private val job = Job()
//
//    private val singleThreadExecutor = Executors.newSingleThreadExecutor()
//
//    override val coroutineContext: CoroutineContext
//        get() = job + singleThreadExecutor.asCoroutineDispatcher()
//
//
//    fun stop() {
//        job.cancel()
//        singleThreadExecutor.shutdown()
//    }
//
//    fun start() = launch {
//        initialDelay?.let {
//            delay(it)
//        }
//        while (isActive) {
//            service.produce()
//            delay(interval)
//        }
//        println("coroutine done")
//    }
//}