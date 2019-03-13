package com.prvz.demointegration.config

import org.springframework.amqp.core.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class RabbitConfig {

    @Bean
    fun exchange(): Exchange =
        ExchangeBuilder
            .topicExchange("int-test-ex")
            .build()

    @Bean
    fun deadLetterQueue(): Queue =
        QueueBuilder
            .durable("dead_letter")
            .build()

    @Bean
    @Primary
    fun queue(): Queue =
        QueueBuilder
            .durable("int-test-q")
            .build()

    @Bean
    @Qualifier("out")
    fun queueOut(): Queue =
        QueueBuilder
            .durable("int-test-q-out")
            .build()

    @Bean
    fun binding(
        queue: Queue,
        exchange: Exchange
    ): Binding =
        BindingBuilder
            .bind(queue)
            .to(exchange)
            .with("int-test-rk")
            .noargs()

    @Bean
    fun binding2(
        @Qualifier("out") queue: Queue,
        exchange: Exchange
    ): Binding =
        BindingBuilder
            .bind(queue)
            .to(exchange)
            .with("int-test-rk-out")
            .noargs()
}