package com.engvie

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class EngvieApplication

fun main(args: Array<String>) {
    runApplication<EngvieApplication>(*args)
}
