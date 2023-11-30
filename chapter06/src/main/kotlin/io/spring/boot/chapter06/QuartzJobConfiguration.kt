package io.spring.boot.chapter06

import org.springframework.batch.core.Job
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.TaskletStep
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class QuartzJobConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
) {

    @Bean
    fun job(): Job {
        println("QuartzJobConfiguration - job")
        return JobBuilder("job", jobRepository)
            .incrementer(RunIdIncrementer())
            .start(step1())
            .build()
    }

    @Bean
    fun step1(): TaskletStep {
        return StepBuilder("step1", jobRepository)
            .tasklet(
                (
                    {
                            contribution, chunkContext ->
                        println("step 1 ran today!")
                        RepeatStatus.FINISHED
                    }
                    ),
                transactionManager,
            )
            .build()
    }
}
