package io.spring.boot.chapter06

import org.springframework.batch.core.Job
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.TaskletStep
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

//@Configuration
class NoRunJob(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
) {

    @Bean
    fun job(): Job {
        return JobBuilder("job", jobRepository)
            .start(step1())
            .build()
    }

    @Bean
    fun step1(): TaskletStep {
        return StepBuilder("step1", jobRepository)
            .tasklet({ contribution, chunkContext ->
                println("step1 run!")
                return@tasklet RepeatStatus.FINISHED
            }, transactionManager)
            .build()
    }
}
