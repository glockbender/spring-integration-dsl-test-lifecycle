package com.prvz.demointegration

import com.prvz.demointegration.service.TestLifecycleService
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DemoIntegrationApplication

fun main(args: Array<String>) {

    val ctx = runApplication<DemoIntegrationApplication>(*args)

    val rabbitTemplate = ctx.getBean(RabbitTemplate::class.java)

    val testLifecycleService = ctx.getBean(TestLifecycleService::class.java)

    testLifecycleService.initLifecycleTest()

    for (i in 0..100) {
        rabbitTemplate.convertAndSend("int-test-ex", "int-test-rk", "TEST MESSAGE $i")
    }


}
