package io.spring.boot.chapter04.batch

import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.job.flow.FlowExecutionStatus
import org.springframework.batch.core.job.flow.JobExecutionDecider
import java.util.*

class RandomDecider : JobExecutionDecider {
    private var random = Random()
    override fun decide(jobExecution: JobExecution, stepExecution: StepExecution?): FlowExecutionStatus {
        return if (random.nextBoolean()) {
            println("FlowExecutionStatus.COMPLETED")
            FlowExecutionStatus(FlowExecutionStatus.COMPLETED.name)
        } else {
            println("FlowExecutionStatus.FAILED")
            FlowExecutionStatus(FlowExecutionStatus.FAILED.name)
        }
    }
}