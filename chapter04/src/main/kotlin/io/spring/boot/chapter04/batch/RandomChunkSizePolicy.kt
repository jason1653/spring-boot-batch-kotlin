package io.spring.boot.chapter04.batch

import org.springframework.batch.repeat.CompletionPolicy
import org.springframework.batch.repeat.RepeatContext
import org.springframework.batch.repeat.RepeatStatus
import java.util.Random

class RandomChunkSizePolicy : CompletionPolicy {
    private var chunkSize = 0
    private var totalProcessed = 0
    private var random = Random()
    override fun isComplete(context: RepeatContext, result: RepeatStatus): Boolean {
        return if (RepeatStatus.FINISHED == result) {
            true
        } else {
            isComplete(context)
        }
    }

    override fun isComplete(context: RepeatContext): Boolean {
        return this.totalProcessed >= chunkSize
    }

    override fun start(parent: RepeatContext): RepeatContext {
        chunkSize = random.nextInt(20)
        totalProcessed = 0
        println("The chunk size has been set to $chunkSize")
        return parent
    }

    override fun update(context: RepeatContext) {
        totalProcessed++
    }
}
