package io.spring.boot.chapter06.domain.support

import io.spring.boot.chapter06.domain.Transaction
import io.spring.boot.chapter06.domain.TransactionDao
import org.springframework.jdbc.core.JdbcTemplate
import java.sql.ResultSet
class TransactionDaoSupport(
    private val jdbcTemplate: JdbcTemplate
) : TransactionDao {

    override fun getTransactionsByAccountNumber(accountNumber: String): List<Transaction> {
        val sql = "select t.id, t.timestamp, t.amount " +
                "from transaction t inner join account_summary a on " +
                "a.id = t.account_summary_id " +
                "where a.account_number = ?"

        return jdbcTemplate.query(sql, arrayOf(accountNumber)) { rs: ResultSet, rowNum: Int ->
            mapRowToTransaction(rs)
        }
    }

    private fun mapRowToTransaction(rs: ResultSet): Transaction {
        val trans = Transaction()
        trans.amount = rs.getDouble("amount")
        trans.timestamp = rs.getTimestamp("timestamp").time
        return trans
    }
}
