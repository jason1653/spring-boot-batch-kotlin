package io.spring.boot.chapter04.job

import org.springframework.batch.core.Job
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.SystemCommandTasklet
import org.springframework.batch.core.step.tasklet.TaskletStep
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

//@Configuration
class SystemCommandJob(
    val jobRepository: JobRepository,
    val transactionManager: PlatformTransactionManager,
) {

    @Bean
    fun job(): Job {
        return JobBuilder("systemCommandJob", jobRepository)
            .start(systemCommandStep())
            .build()
    }

    @Bean
    fun systemCommandStep(): TaskletStep {
        return StepBuilder("systemCommandStep", jobRepository)
            .tasklet(systemCommandTasklet(), transactionManager)
            .build()
    }

    @Bean
    fun systemCommandTasklet(): SystemCommandTasklet {
        val systemCommandTasklet = SystemCommandTasklet()
        systemCommandTasklet.setCommand("rm -rf /tmp.txt")
        systemCommandTasklet.setTimeout(5000)
        systemCommandTasklet.setInterruptOnCancel(true)

        return systemCommandTasklet
    }
}
