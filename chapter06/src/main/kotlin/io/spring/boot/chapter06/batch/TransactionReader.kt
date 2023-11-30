package io.spring.boot.chapter06.batch

import io.spring.boot.chapter06.domain.Transaction
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.annotation.AfterStep
import org.springframework.batch.item.ExecutionContext
import org.springframework.batch.item.ItemStreamReader
import org.springframework.batch.item.file.transform.FieldSet

class TransactionReader(
    private var fieldSetReader: ItemStreamReader<FieldSet>,
) : ItemStreamReader<Transaction> {
    private var recordCount: Int = 0
    private var expectedReocrdCount: Int = 0

    override fun read(): Transaction? {
        return fieldSetReader.read()?.let { process(it) }
    }

    override fun open(executionContext: ExecutionContext) {
        fieldSetReader.open(executionContext)
    }

    override fun update(executionContext: ExecutionContext) {
        fieldSetReader.update(executionContext)
    }

    override fun close() {
        fieldSetReader.close()
    }

    private fun process(fieldSet: FieldSet): Transaction? {
        var result: Transaction? = null

        if (fieldSet != null) {
            if (fieldSet.fieldCount > 1) {
                result = Transaction()
                result.accountNumber = fieldSet.readString(0)
                result.timestamp = fieldSet.readDate(1, "yyyy-MM-dd HH:mm:ss").time
                result.amount = fieldSet.readDouble(2)

                recordCount++
            } else {
                expectedReocrdCount = fieldSet.readInt(0)
            }
        }

        return result
    }

    @AfterStep
    fun afterStep(execution: StepExecution): ExitStatus? {
        if (recordCount == expectedReocrdCount) {
            return execution.exitStatus
        } else {
            return ExitStatus.STOPPED
        }
    }
}
