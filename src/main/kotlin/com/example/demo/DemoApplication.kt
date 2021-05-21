package com.example.demo

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.util.*
import java.util.concurrent.atomic.AtomicInteger


@SpringBootApplication
class DemoApplication {
    // Using the default logger
    private val logger: Logger = LoggerFactory.getLogger(DemoApplication::class.java)
    private val random = Random()
    private val count: AtomicInteger = AtomicInteger(0)


    // Publishes a random integer to the "ints" channel (as defined
    // in the application.yml file) every second.
    @Bean
    fun send(): suspend () -> Flow<Int> = {
        flow {
            while (true) {
                delay(1000)
                emit(random.nextInt(100))
            }
        }
    }

    // Subscribes to the "ints" channel, calculating an
    // accumulated total, and publishing both to the "total" channel
    @Bean
    fun accumulate(): suspend (Flow<Int>) -> Flow<String> = {
        it.map { payload ->
            "Current value: $payload, Total: ${count.addAndGet(payload)}"
        }
    }

    //Subscribe to the "total" channel and log the results
    @Bean
    fun receive(): suspend (Flow<String>) -> Unit = {
        it.collect { payload -> logger.info(payload) }
    }

//    // todo: workable analog using flux
//    // Subscribe to the "total" channel and log the results
//    @Bean
//    fun receive(): Consumer<Flux<String>> = Consumer {
//        it.doOnNext { payload -> logger.info(payload) }.subscribe()
//    }
}

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
