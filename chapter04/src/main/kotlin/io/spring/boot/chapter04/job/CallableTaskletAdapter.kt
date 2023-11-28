package io.spring.boot.chapter04.job

import org.springframework.batch.core.repository.JobRepository
import org.springframework.transaction.PlatformTransactionManager


class CallableTaskletAdapter(
    val jobRepository: JobRepository,
    val transactionManager: PlatformTransactionManager,
) {

    fun tasklet() {
        val callableTaskletAdapter = CallableTaskletAdapter(jobRepository, transactionManager)

    }
}