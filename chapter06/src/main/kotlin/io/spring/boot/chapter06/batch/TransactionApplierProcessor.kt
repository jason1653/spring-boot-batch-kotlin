package io.spring.boot.chapter06.batch

import io.spring.boot.chapter06.domain.AccountSummary
import io.spring.boot.chapter06.domain.TransactionDao
import org.springframework.batch.item.ItemProcessor

class TransactionApplierProcessor(
    private val transactionDao: TransactionDao
) : ItemProcessor<AccountSummary, AccountSummary> {
    override fun process(item: AccountSummary): AccountSummary? {
        val transactions = transactionDao.getTransactionsByAccountNumber(item.accountNumber)
        transactions.forEach { transaction ->
            item.currentBalance += transaction.amount
        }
        return item
    }
}