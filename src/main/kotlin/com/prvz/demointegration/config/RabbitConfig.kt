package com.prvz.demointegration.config

import org.springframework.amqp.core.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

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
    fun queue(): Queue =
        QueueBuilder
            .durable("int-test-q")
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
}