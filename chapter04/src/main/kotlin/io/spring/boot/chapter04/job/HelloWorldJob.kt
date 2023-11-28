package io.spring.boot.chapter04.job

import io.spring.boot.chapter04.batch.DailyJobTimestamper
import io.spring.boot.chapter04.batch.JobLoggerListener
import io.spring.boot.chapter04.batch.ParameterValidator
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.CompositeJobParametersValidator
import org.springframework.batch.core.job.DefaultJobParametersValidator
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
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
        return JobBuilder("basicJob", jobRepository)
            .start(step1()) // step 등록
//            .validator(validator()) // 커스텀 validator 등록
            .incrementer(DailyJobTimestamper())
            .listener(JobLoggerListener())
            .build()
    }

    @Bean
    fun step1(): Step {
        System.out.println("HelloWorldJob.step1()")
        return StepBuilder("step1", jobRepository)
            .tasklet(helloWorldTasklet(null, null), transactionManager)
            .build()
    }

    @Bean
    fun validator(): CompositeJobParametersValidator {
        val validator = CompositeJobParametersValidator()
        val defaultJobParametersValidator = DefaultJobParametersValidator(arrayOf("fileName"), arrayOf("name", "ㅇcurentDate"))

        defaultJobParametersValidator.afterPropertiesSet()

        validator.setValidators(
            listOf(
                ParameterValidator(),
                defaultJobParametersValidator,
            ),
        )

        return validator
    }

    @StepScope
    @Bean
    fun helloWorldTasklet(
        @Value("#{jobParameters['name']}") name: String?,
        @Value("#{jobParameters['fileName']}") fileName: String?,
    ): Tasklet {
        System.out.println("HelloWorldJob.helloWorldTasklet()")

        return Tasklet { contribution, chunkContext ->
            if (name == null) {
                println("Hello, World!")
            } else {
                println("Hello, $name")
            }

            if (fileName == null) {
                println("fileName = null")
            } else {
                println("fileName = $fileName")
            }
            RepeatStatus.FINISHED
        }
    }
}
