package io.spring.boot.chapter04.job

import org.springframework.batch.core.Job
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.CallableTaskletAdapter
import org.springframework.batch.core.step.tasklet.TaskletStep
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class CallableTaskletConfiguration(
    val jobRepository: JobRepository,
    val transactionManager: PlatformTransactionManager,
) {

    @Bean
    fun callableJob(): Job {
        return JobBuilder("callableJob", jobRepository)
            .start(callableStep())
            .build()
    }

    @Bean
    fun callableStep(): TaskletStep {
        return StepBuilder("callableStep", jobRepository)
            .tasklet(tasklet(), transactionManager)
            .build()
    }

    @Bean
    fun callableObject(): RepeatStatus {
        println("This was executed in another thread")
        return RepeatStatus.FINISHED
    }

    @Bean
    fun tasklet(): CallableTaskletAdapter {
        val callableTaskletAdapter = CallableTaskletAdapter()
        callableTaskletAdapter.setCallable(::callableObject)

        return callableTaskletAdapter
    }
}
