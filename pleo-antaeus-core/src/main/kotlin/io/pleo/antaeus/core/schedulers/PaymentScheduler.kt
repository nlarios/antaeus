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

//Scheduler class
class PaymentScheduler() {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    // schedule next payment for all customers
    fun scheduleNextPayment(billingAction: ((String) -> List<Billing?>?), date: Date = calculateNextBillingDate()) {
        logger.info("Schedule next payment for all users at $date")
        Timer("SettingUpBillingSchedule", false).schedule(time = date) {

            try {
                billingAction(DateTimeFormatter.ISO_INSTANT.format(date.toInstant()))
            } catch (e: Exception) {
                logger.error("billingAction for all customers failed with exception: ${e.message}")
                throw BillingServiceException("At multiple customers")
            } finally {
                scheduleNextPayment(billingAction)
            }
        }
    }

    // schedule next payment for one customers
    fun scheduleNextPayment(billingAction: ((Int, timestamp: String) -> Billing?), id: Int, date: Date = calculateNextBillingDate()) {
        logger.info("Schedule next payment for user with id: $id at $date")

        Timer("SettingUpBillingSchedule", false).schedule(time = date) {
            try {
                billingAction(id, DateTimeFormatter.ISO_INSTANT.format(Instant.now()))
            } catch (e: Exception) {
                logger.error("billingAction for customer $id failed with exception: ${e.message}")
                throw BillingServiceException("At multiple customers")
            } finally {
                scheduleNextPayment(billingAction, id)
            }
        }
    }

    fun calculateNextBillingDate(): Date {
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
