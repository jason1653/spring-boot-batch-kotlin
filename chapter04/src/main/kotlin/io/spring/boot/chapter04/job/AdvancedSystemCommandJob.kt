package io.spring.boot.chapter04.job

import org.springframework.batch.core.Job
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.SimpleSystemProcessExitCodeMapper
import org.springframework.batch.core.step.tasklet.SystemCommandTasklet
import org.springframework.batch.core.step.tasklet.TaskletStep
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.transaction.PlatformTransactionManager

//@Configuration
class AdvancedSystemCommandJob(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
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
        val tasklet = SystemCommandTasklet()
        tasklet.setCommand("touch tmp.txt")
        tasklet.setTimeout(5000)
        tasklet.setInterruptOnCancel(true)

        tasklet.setWorkingDirectory("/Users/jason/spring-batch")

        tasklet.setSystemProcessExitCodeMapper(touchCodeMapper())
        tasklet.setTerminationCheckInterval(5000)
        tasklet.setTaskExecutor(SimpleAsyncTaskExecutor())

        tasklet.setEnvironmentParams(arrayOf("JAVA_HOME=/usr/bin/java", "BATCH_HOME=/Users/jason/spring-batch"))

        return tasklet
    }

    @Bean
    fun touchCodeMapper(): SimpleSystemProcessExitCodeMapper {
        return SimpleSystemProcessExitCodeMapper()
    }
}
