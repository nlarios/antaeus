package io.pleo.antaeus.core.schedulers

import io.pleo.antaeus.core.exceptions.BillingServiceException
import io.pleo.antaeus.models.Billing
import mu.KotlinLogging
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.schedule
import java.util.Calendar


class BillingServiceScheduler() {
    companion object {
        private val logger = KotlinLogging.logger {}
    }


    fun scheduleNextBillingTime(billingAction: (() -> Unit?),  date: Date = calculateNextBillingDate()) {
//        var next  = calculateTestDate()
        logger.info("Schedule next payment for all users at $date")
        Timer("SettingUpBillingSchedule", false).schedule(time = date) {

            try {
                billingAction()
            }
            catch (e: Exception) {
                logger.error("billingAction failed with exception: ${e.message}")
                throw BillingServiceException("At multiple customers")
            }
            finally {
                scheduleNextBillingTime(billingAction)
            }
        }
    }

    fun scheduleNextBillingTime(billingAction: ((Int, timestamp: String) -> Billing?), id: Int, date: Date = calculateNextBillingDate()) {
//        var nextTime = calculateNextBillingDate()
//        var date = calculateTestDate()
        logger.info("Schedule next payment for user with id: $id at $date")

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
        //println(calendar.time)
        return calendar.time
    }
}
