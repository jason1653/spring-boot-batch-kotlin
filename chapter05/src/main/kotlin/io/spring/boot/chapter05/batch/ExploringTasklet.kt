package io.spring.boot.chapter05.batch

import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.explore.JobExplorer
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus

class ExploringTasklet(
    private val explorer: JobExplorer,
) : Tasklet {

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus {
        val jobName = chunkContext.stepContext.jobParameters["instance"] as String? ?: "explorerJob"

        val instances = explorer.getJobInstances(jobName, 0, Int.MAX_VALUE)

        println(String.format("There are %s job instances for the job %s", instances.size, jobName))

        println("********************")

        for (instance in instances) {
            val jobExecutions = explorer.getJobExecutions(instance)

            println(String.format("Instance %d had %d executions", instance.instanceId, jobExecutions.size))

            for (jobExecution in jobExecutions) {
                println(String.format("Execution %s has status %s", jobExecution.id, jobExecution.status))
            }
        }

        return RepeatStatus.FINISHED
    }
}
