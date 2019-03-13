package com.prvz.demointegration.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.integration.amqp.dsl.AmqpInboundChannelAdapterSMLCSpec
import org.springframework.stereotype.Service
import java.util.concurrent.ExecutorService

@Service
class TestLifecycleServiceImpl(
    private val executorService: ExecutorService,
    private val amqpInboundChannelAdapterSMLCSpec: AmqpInboundChannelAdapterSMLCSpec
) : TestLifecycleService {

    private val logger: Logger = LoggerFactory.getLogger(TestLifecycleServiceImpl::class.java)

    override fun initLifecycleTest() {
        executorService.submit {
            for (i in 0..10) {
                logger.info("LIFECYCLE ITERATION: {}", i)
                Thread.sleep(100)
                amqpInboundChannelAdapterSMLCSpec.stop { logger.info("STOP CALLBACK: {}", i) }
                logger.info("STOPPED: {}", i)
                Thread.sleep(2000)
                amqpInboundChannelAdapterSMLCSpec.start()
                logger.info("STARTED: {}", i)
            }
        }
    }
}