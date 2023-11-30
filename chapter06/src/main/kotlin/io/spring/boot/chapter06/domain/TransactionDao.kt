package io.spring.boot.chapter06.domain

interface TransactionDao {
    fun getTransactionsByAccountNumber(accountNumber: String): List<Transaction>
}