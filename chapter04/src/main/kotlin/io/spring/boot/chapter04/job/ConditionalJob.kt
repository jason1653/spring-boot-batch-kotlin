package io.spring.boot.chapter04.job

import io.spring.boot.chapter04.batch.RandomDecider
import org.springframework.batch.core.Job
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.core.step.tasklet.TaskletStep
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

//@Configuration
class ConditionalJob(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
) {

    @Bean
    fun passTasklet(): Tasklet {
        return Tasklet { contribution: StepContribution?, chunkContext: ChunkContext? ->
            println("Pass!")
//            return@Tasklet RepeatStatus.FINISHED
            throw RuntimeException("Causing a failure")
        }
    }

    @Bean
    fun successTasklet(): Tasklet {
        return Tasklet { contribution: StepContribution?, chunkContext: ChunkContext? ->
            println("Success!")
            return@Tasklet RepeatStatus.FINISHED
        }
    }

    @Bean
    fun failTasklet(): Tasklet {
        return Tasklet { contribution: StepContribution?, chunkContext: ChunkContext? ->
            println("Failure!")
            return@Tasklet RepeatStatus.FINISHED
        }
    }

    @Bean
    fun job(): Job {
        /*
        return JobBuilder("conditionalJob", jobRepository)
            .start(firstStep())
            .next(decider())
            .from(decider())
            .on("FAILED").to(failureStep())
            .from(decider())
            .on("*").to(successStep())
            .end()
            .build()
         */

        /*
        return JobBuilder("conditionalJob", jobRepository)
            .start(firstStep())
            .on("FAILED").end()
            .from(firstStep()).on("*").to(successStep())
            .end()
            .build()

         */

        return JobBuilder("conditionalJob", jobRepository)
            .start(firstStep())
            .on("FAILED").stopAndRestart(successStep())
            .from(firstStep()).on("*").to(successStep())
            .end()
            .build()
    }

    @Bean
    fun firstStep(): TaskletStep {
        return StepBuilder("firstStep", jobRepository)
            .tasklet(passTasklet(), transactionManager)
            .allowStartIfComplete(true)
            .build()
    }

    @Bean
    fun successStep(): TaskletStep {
        return StepBuilder("successStep", jobRepository)
            .tasklet(successTasklet(), transactionManager)
            .allowStartIfComplete(true)
            .build()
    }

    @Bean
    fun failureStep(): TaskletStep {
        return StepBuilder("failureStep", jobRepository)
            .tasklet(failTasklet(), transactionManager)
            .allowStartIfComplete(true)
            .build()
    }

    @Bean
    fun decider(): RandomDecider {
        return RandomDecider()
    }
}
