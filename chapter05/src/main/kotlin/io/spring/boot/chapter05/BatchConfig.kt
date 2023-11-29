package io.spring.boot.chapter05

import io.spring.boot.chapter05.batch.ExploringTasklet
import org.springframework.batch.core.Job
import org.springframework.batch.core.explore.JobExplorer
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.TaskletStep
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class BatchConfig(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val jobExplorer: JobExplorer,
) {
    @Bean
    fun explorerTasklet(): ExploringTasklet {
        return ExploringTasklet(jobExplorer)
    }

    @Bean
    fun explorerStep(): TaskletStep {
        return StepBuilder("explorerStep", jobRepository)
            .tasklet(explorerTasklet(), transactionManager)
            .build()
    }

    @Bean
    fun explorerJob(): Job {
        return JobBuilder("explorerJob", jobRepository)
            .start(explorerStep())
            .build()
    }
}
