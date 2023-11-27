package io.spring.boot.chapter04.batch

import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.JobParametersIncrementer

class DailyJobTimestamper : JobParametersIncrementer {
    override fun getNext(parameters: JobParameters?): JobParameters {
        return JobParametersBuilder(parameters!!)
            .addDate("currentDate", java.util.Date())
            .toJobParameters()
    }
}
