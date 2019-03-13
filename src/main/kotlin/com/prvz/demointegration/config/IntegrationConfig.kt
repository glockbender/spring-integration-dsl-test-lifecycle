package com.prvz.demointegration.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.amqp.dsl.Amqp
import org.springframework.integration.amqp.dsl.AmqpInboundChannelAdapterSMLCSpec
import org.springframework.integration.annotation.IntegrationComponentScan
import org.springframework.integration.config.EnableIntegration
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.MessageChannels
import org.springframework.integration.handler.GenericHandler
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.MessageBuilder
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@Configuration
@EnableIntegration
@IntegrationComponentScan
class IntegrationConfig {

    private val logger: Logger = LoggerFactory.getLogger(IntegrationConfig::class.java)

    @Bean
    fun executorService(): ExecutorService = Executors.newCachedThreadPool()

    @Bean
    fun outChannel(): MessageChannel = MessageChannels.queue().get()

    @Bean
    fun amqpInboundSpec(
        queue: Queue,
        connectionFactory: ConnectionFactory
    ): AmqpInboundChannelAdapterSMLCSpec =
        Amqp.inboundAdapter(connectionFactory, queue)
            .recoveryCallback { println("RECOVERY") }

    @Bean
    fun testHandler(): GenericHandler<String> = GenericHandler { payload, headers ->
        logger.info("HANDLING PAYLOAD: {}", payload)
        return@GenericHandler MessageBuilder.createMessage(payload, headers)
    }

    @Bean
    fun testIntFlow(
        amqpInboundSpec: AmqpInboundChannelAdapterSMLCSpec,
        outChannel: MessageChannel
    ): IntegrationFlow = IntegrationFlows
        .from(amqpInboundSpec)
        .handle(testHandler())
        .channel(outChannel)
        .get()

}