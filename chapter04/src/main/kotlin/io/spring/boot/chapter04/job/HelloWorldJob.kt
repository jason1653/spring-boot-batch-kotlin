package io.spring.boot.chapter04.job

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

//@Configuration
class HelloWorldJob(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
) {

    @Bean
    fun job(): Job {
        System.out.println("HelloWorldJob.job()")
        return JobBuilder("basicJob", jobRepository).start(step1()).build()
    }

    @Bean
    fun step1(): Step {
        System.out.println("HelloWorldJob.step1()")
        return StepBuilder("step1", jobRepository).tasklet(helloWorldTasklet(name = null), transactionManager).allowStartIfComplete(true).build()
    }

    @StepScope
    @Bean
    fun helloWorldTasklet(
        @Value("#{jobParameters['name']}") name: String? = null,
    ): Tasklet {
        System.out.println("HelloWorldJob.helloWorldTasklet()")

        return Tasklet { contribution, chunkContext ->
            if (name == null) {
                println("Hello, World!")
            } else {
                println("Hello, $name")
            }
            RepeatStatus.FINISHED
        }
    }
}
