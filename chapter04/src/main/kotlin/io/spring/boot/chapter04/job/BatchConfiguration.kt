package io.spring.boot.chapter04.job

import io.spring.boot.chapter04.batch.HelloWorldTasklet
import org.springframework.batch.core.Job
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.listener.ExecutionContextPromotionListener
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.TaskletStep
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

//@Configuration
class BatchConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
) {

    @Bean
    fun job(): Job {
        return JobBuilder("job", jobRepository)
            .start(step1())
            .next(step2())
            .build()
    }

    @Bean
    fun step1(): TaskletStep {
        return StepBuilder("step1", jobRepository)
            .tasklet(HelloWorldTasklet(), transactionManager)
            .listener(promotionListener())
            .build()
    }

    @Bean
    fun step2(): TaskletStep {
        return StepBuilder("step2", jobRepository)
            .tasklet(
                { contribution, chunkContext ->
                    println("Hello, World! - step2")
                    RepeatStatus.FINISHED
                },
                transactionManager,
            )
            .build()
    }

    @Bean
    fun promotionListener(): ExecutionContextPromotionListener {
        val listener = ExecutionContextPromotionListener()

        listener.setKeys(arrayOf("user.name"))

        return listener
    }
}