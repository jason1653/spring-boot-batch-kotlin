package io.spring.boot.chapter04.batch

import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.item.ExecutionContext
import org.springframework.batch.repeat.RepeatStatus

class HelloWorldTasklet : Tasklet {
    private val HELLO_WORLD: String = "Hello, %s"
    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        val name: String = chunkContext.stepContext.jobParameters.get("name") as String

        val jobContext: ExecutionContext = chunkContext
            .stepContext
            .stepExecution
            .jobExecution
            .executionContext

        jobContext.put("user.name", name)

        println(String.format(HELLO_WORLD, name))
        return RepeatStatus.FINISHED
    }
}
