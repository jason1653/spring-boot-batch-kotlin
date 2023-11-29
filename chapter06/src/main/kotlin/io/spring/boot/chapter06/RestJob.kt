package io.spring.boot.chapter06

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.repository.JobRepository
import org.springframework.context.ApplicationContext
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.Properties

class RestJob(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
) {
    @RestController
    class JobLaunchingController(
        private val jobLauncher: JobLauncher,
        private val context: ApplicationContext,
    ) {
        @PostMapping("/run")
        fun runJob(@RequestBody request: JobLaunchRequest) {
            val job = context.getBean(request.name, Job::class.java)

        }
    }

    class JobLaunchRequest {
        val name: String = ""
        val jobParameters: Properties = Properties()

        fun getJobParameters() {
            val properties = Properties()
            properties.putAll(jobParameters)

//            return JobParametersBuilder(properties)
        }
    }
}
