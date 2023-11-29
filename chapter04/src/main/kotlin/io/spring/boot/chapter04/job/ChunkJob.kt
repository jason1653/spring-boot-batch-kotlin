package io.spring.boot.chapter04.job

import io.spring.boot.chapter04.batch.LoggingStepStartStopListener
import io.spring.boot.chapter04.batch.RandomChunkSizePolicy
import org.springframework.batch.core.Job
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.TaskletStep
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.support.ListItemReader
import org.springframework.batch.repeat.policy.CompositeCompletionPolicy
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy
import org.springframework.batch.repeat.policy.TimeoutTerminationPolicy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import java.util.*

@Configuration
class ChunkJob(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
) {

    @Bean
    fun chunkBasedJob(): Job {
        return JobBuilder("chunkBasedJob", jobRepository)
            .start(chunkStep())
            .build()
    }

    @Bean
    fun chunkStep(): TaskletStep {
        return StepBuilder("chunkStep", jobRepository)
//            .chunk<String, String>(100, transactionManager)
            .chunk<String, String>(randomCompletionPolicy(), transactionManager)
            .reader(itemReader())
            .writer(itemWriter())
            .allowStartIfComplete(true)
            .listener(LoggingStepStartStopListener())
            .build()
    }

    @Bean
    fun itemReader(): ListItemReader<String> {
        val items: MutableList<String> = ArrayList(100000)
        for (i in 0..99999) {
            items.add(UUID.randomUUID().toString())
        }
        return ListItemReader(items)
    }

    @Bean
    fun itemWriter(): (Chunk<out String>) -> Unit {
        return { items: Chunk<out String> ->
            for (item in items) {
                println(">> current item = $item")
            }
        }
    }

    @Bean
    fun completionPolicy(): CompositeCompletionPolicy {
        val policy = CompositeCompletionPolicy()

        policy.setPolicies(
            arrayOf(
                TimeoutTerminationPolicy(3),
                SimpleCompletionPolicy(1000),
            ),
        )

        return policy
    }

    @Bean
    fun randomCompletionPolicy(): RandomChunkSizePolicy {
        return RandomChunkSizePolicy()
    }
}
