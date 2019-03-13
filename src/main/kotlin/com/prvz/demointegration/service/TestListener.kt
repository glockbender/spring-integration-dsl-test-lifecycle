package com.prvz.demointegration.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class TestListener {

    private val logger: Logger = LoggerFactory.getLogger(TestListener::class.java)

    @RabbitListener(queues = ["int-test-q-out"], concurrency = "1-1", autoStartup = "true")
    fun processReportRequest(message: String) {
        logger.info("MESSAGE RECIEVED: {}", message)
    }

}