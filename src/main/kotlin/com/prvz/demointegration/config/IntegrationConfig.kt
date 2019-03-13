package com.prvz.demointegration.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.amqp.dsl.Amqp
import org.springframework.integration.amqp.dsl.AmqpInboundGatewaySMLCSpec
import org.springframework.integration.amqp.dsl.AmqpOutboundEndpointSpec
import org.springframework.integration.annotation.IntegrationComponentScan
import org.springframework.integration.config.EnableIntegration
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.Pollers
import org.springframework.integration.handler.GenericHandler
import org.springframework.integration.scheduling.PollerMetadata
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
    fun outQueueSpec(rabbitTemplate: RabbitTemplate): AmqpOutboundEndpointSpec =
        Amqp.outboundAdapter(rabbitTemplate)
            .exchangeName("int-test-ex")
            .routingKey("int-test-rk-out")

    @Bean
    fun amqpInboundSpec(
        queue: Queue,
        connectionFactory: ConnectionFactory,
        rabbitTemplate: RabbitTemplate
    ): AmqpInboundGatewaySMLCSpec =
        Amqp.inboundGateway(connectionFactory, rabbitTemplate, queue)
            .recoveryCallback { logger.info("RECOVERY") }

    @Bean
    fun testHandler(): GenericHandler<String> = GenericHandler { payload, headers ->
        logger.info("HANDLING PAYLOAD: {}", payload)
        //VERY HIGH LOAD TASK!!!
        for (i in 0..250000) {
            ". . . . . . . . . . . . . . . . . . . . . . . . . ."
                .split(".")
                .onEach { it.trim() }
                .joinToString()
        }
        return@GenericHandler MessageBuilder.createMessage(payload, headers)
    }

    @Bean(name = [PollerMetadata.DEFAULT_POLLER])
    fun poller(): PollerMetadata {
        return Pollers.fixedRate(500).get()
    }

    @Bean
    fun testIntFlow(
        amqpInboundGatewaySMLCSpec: AmqpInboundGatewaySMLCSpec,
        outQueueSpec: AmqpOutboundEndpointSpec
    ): IntegrationFlow = IntegrationFlows
        .from(amqpInboundGatewaySMLCSpec)
        .handle(testHandler())
        .handle(outQueueSpec.get())
        .get()

}