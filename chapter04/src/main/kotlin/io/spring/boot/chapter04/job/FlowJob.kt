package io.spring.boot.chapter04.job

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.FlowBuilder
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.job.flow.Flow
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.core.step.tasklet.TaskletStep
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class FlowJob(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
) {
    @Bean
    fun loadStockFile(): Tasklet {
        return Tasklet { contribution, chunkContext ->
            println("The stock file has been loaded")
            return@Tasklet RepeatStatus.FINISHED
        }
    }

    @Bean
    fun loadCustomerFile(): Tasklet {
        return Tasklet { contribution, chunkContext ->
            println("The customer file has been loaded")
            return@Tasklet RepeatStatus.FINISHED
        }
    }

    @Bean
    fun updateStart(): Tasklet {
        return Tasklet { contribution, chunkContext ->
            println("The start has been updated")
            return@Tasklet RepeatStatus.FINISHED
        }
    }

    @Bean
    fun runBatchTasklet(): Tasklet {
        return Tasklet { contribution, chunkContext ->
            println("The batch has been run")
            return@Tasklet RepeatStatus.FINISHED
        }
    }

    @Bean
    fun preProcessingFlow(): Flow {
        return FlowBuilder<Flow>("preProcessingFlow")
            .start(loadFileStep())
            .next(loadCustomerStep())
            .next(updateStartStep())
            .build()
    }

    @Bean
    fun conditionalStepLogicJob(): Job {
        /*
        return JobBuilder("conditionalStepLogicJob", jobRepository)
            .start(preProcessingFlow())
            .next(runBatch())
            .end()
            .build()

         */

        return JobBuilder("conditionalStepLogicJob", jobRepository)
            .start(intializeBatch())
            .next(runBatch())
            .build()
    }

    @Bean
    fun intializeBatch(): Step {
        return StepBuilder("intializeBatch", jobRepository)
            .flow(preProcessingFlow())
            .allowStartIfComplete(true)
            .build()
    }

    @Bean
    fun loadFileStep(): TaskletStep {
        return StepBuilder("loadFileStep", jobRepository)
            .tasklet(loadStockFile(), transactionManager)
            .allowStartIfComplete(true)
            .build()
    }

    @Bean
    fun loadCustomerStep(): TaskletStep {
        return StepBuilder("loadCustomerStep", jobRepository)
            .tasklet(loadCustomerFile(), transactionManager)
            .allowStartIfComplete(true)
            .build()
    }

    @Bean
    fun updateStartStep(): TaskletStep {
        return StepBuilder("updateStartStep", jobRepository)
            .tasklet(updateStart(), transactionManager)
            .allowStartIfComplete(true)
            .build()
    }

    @Bean
    fun runBatch(): TaskletStep {
        return StepBuilder("runBatch", jobRepository)
            .tasklet(runBatchTasklet(), transactionManager)
            .allowStartIfComplete(true)
            .build()
    }
}
