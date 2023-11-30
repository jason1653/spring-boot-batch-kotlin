package io.spring.boot.chapter06

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.TaskletStep
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

//@EnableBatchProcessing
//@Configuration
class RestJob(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
) {

    @Bean
    fun job(): Job {
        println("Job start")
        return JobBuilder("job", jobRepository)
            .incrementer(RunIdIncrementer())
            .start(step1())
            .build()
    }

    @Bean
    fun step1(): TaskletStep {
        println("step1 start")

        return StepBuilder("step1", jobRepository)
            .tasklet(
                (
                    { contribution, chunkContext ->
                        println("step 1 ran today!")
                        RepeatStatus.FINISHED
                    }
                    ),
                transactionManager,
            )
            .allowStartIfComplete(true)
            .build()
    }

    @RestController
    class JobLaunchingController(
        private val jobLauncher: JobLauncher,
        private val context: ApplicationContext,
    ) {
        @PostMapping("/run")
        fun runJob(@RequestBody request: JobLaunchRequest): JobExecution {
            val job = context.getBean(request.name, Job::class.java)
            println("post /run")

            return jobLauncher.run(job, request.getJobParameters())
        }
    }

    class JobLaunchRequest {
        var name: String = ""
        var jobParamsProperties: Properties = Properties()

        fun getJobParameters(): JobParameters {
            println("getJobParameters")
            val jobParametersBuilder = JobParametersBuilder()

            for ((key, value) in jobParamsProperties) {
                jobParametersBuilder.addString(key.toString(), value.toString())
            }

            return jobParametersBuilder.toJobParameters()
        }
    }
}
