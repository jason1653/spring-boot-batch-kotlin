package io.spring.boot.chapter04.batch

import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.annotation.AfterJob
import org.springframework.batch.core.annotation.BeforeJob

class JobLoggerListener {
    private val START_MESSAGE: String = "%s is beginning execution"
    private val END_MESSAGE: String = "%s has completed with the status %s"

    @BeforeJob
    fun beforeJob(jobExecution: JobExecution) {
        System.out.println(String.format(START_MESSAGE, jobExecution.jobInstance.jobName))
    }

    @AfterJob
    fun afterJob(jobExecution: JobExecution) {
        System.out.println(String.format(END_MESSAGE, jobExecution.jobInstance.jobName, jobExecution.status))
    }
}