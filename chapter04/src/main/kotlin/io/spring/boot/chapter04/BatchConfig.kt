package io.spring.boot.chapter04

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class BatchConfig(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
) {

    @Bean
    fun step1(): Step {
        return StepBuilder("basicJob", jobRepository).tasklet({ contribution, chunkContext ->
            println("Hello, World!")
            RepeatStatus.FINISHED
        }, transactionManager).build()
    }

    @Bean
    fun job(): Job {
        return JobBuilder("step1", jobRepository).start(step1()).build()
    }
}