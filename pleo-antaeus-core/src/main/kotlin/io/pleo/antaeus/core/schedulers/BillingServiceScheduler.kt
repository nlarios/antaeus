package io.pleo.antaeus.core.schedulers

import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

class BillingServiceScheduler {
//(val service: BillingServiceScheduler, val interval: Long, val initialDelay: Long?) :
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
}