package io.pleo.antaeus.core.schedulers

import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule
import java.util.Calendar


class BillingServiceScheduler() {


    fun scheduleNextBillingTime(billingAction: () -> Unit) {

        Timer("SettingUpBilling", false).schedule(time = calculateNextBillingDate()) {
            billingAction
            val nextTime = calculateNextBillingDate()
            Timer("SettingUpNextBilling", false).schedule(time = nextTime) {
                scheduleNextBillingTime(billingAction)
            }
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