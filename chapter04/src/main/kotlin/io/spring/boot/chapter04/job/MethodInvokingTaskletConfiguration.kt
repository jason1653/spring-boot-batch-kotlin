package io.spring.boot.chapter04.job

import io.spring.boot.chapter04.service.CustomService
import org.springframework.batch.core.Job
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter
import org.springframework.batch.core.step.tasklet.TaskletStep
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class MethodInvokingTaskletConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
) {

    @Bean
    fun methodInvokingJob(): Job {
        return JobBuilder("methodInvokingJob", jobRepository)
            .start(methodInvokingStep())
            .build()
    }
    @Bean
    fun methodInvokingStep(): TaskletStep {
        return StepBuilder("methodInvokingStep", jobRepository)
            .tasklet(methodInvokingTasklet(null), transactionManager)
            .build()
    }

    @StepScope
    @Bean
    fun methodInvokingTasklet(
        @Value("#{jobParameters['message']}") message: String?,
    ): MethodInvokingTaskletAdapter {
        val methodInvokingTaskletAdapter = MethodInvokingTaskletAdapter()
        methodInvokingTaskletAdapter.setTargetObject(service())
        methodInvokingTaskletAdapter.setTargetMethod("serviceMethod")
        methodInvokingTaskletAdapter.setArguments(arrayOf(message))

        return methodInvokingTaskletAdapter
    }

    @Bean
    fun service(): CustomService {
        return CustomService()
    }
}
