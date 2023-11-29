package io.spring.boot.chapter06

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.Properties

@SpringBootApplication
class Chapter06Application

fun main(args: Array<String>) {
    Properties().apply {
        setProperty("spring.batch.job.enabled", "false")
    }
    runApplication<Chapter06Application>(*args)
}
