package io.spring.boot.chapter06

import io.spring.boot.chapter06.batch.TransactionReader
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.batch.item.file.mapping.PassThroughFieldSetMapper
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer
import org.springframework.batch.item.file.transform.FieldSet
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.core.io.Resource
import org.springframework.transaction.PlatformTransactionManager

class TransactionProcessingJob(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
) {

    @StepScope
    @Bean
    fun transactionReader(): TransactionReader? {
        return fileItemReader(null)?.let { TransactionReader(it) }

    }

    @StepScope
    @Bean
    fun fileItemReader(
        @Value("#{jobParameters['inputFile']}") inputFile: Resource?
    ): FlatFileItemReader<FieldSet>? {
        return inputFile?.let {
            FlatFileItemReaderBuilder<FieldSet>()
                .name("fileItemReader")
                .resource(it)
                .lineTokenizer(DelimitedLineTokenizer())
                .fieldSetMapper(PassThroughFieldSetMapper())
                .build()
        }
    }

}