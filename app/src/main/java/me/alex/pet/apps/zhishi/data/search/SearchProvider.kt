package me.alex.pet.apps.zhishi.data.search

import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.alex.pet.apps.zhishi.RuleQueries
import me.alex.pet.apps.zhishi.domain.search.SearchRepository
import me.alex.pet.apps.zhishi.domain.search.SearchResult
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.log2

class SearchProvider(
        private val ruleQueries: RuleQueries,
        private val sqlDriver: SqlDriver
) : SearchRepository {

    private val suggestions = listOf(
            "140",
            "двойное н",
            "пре при",
            "не ни",
            "гар гор",
            "союзы",
            "обращения",
            "правила переноса",
            "тире",
            "прямая речь"
    )

    override fun getSuggestions(): List<String> {
        return suggestions
    }

    override suspend fun queryRulesById(numbers: List<Long>): List<SearchResult> {
        return withContext(Dispatchers.IO) {
            ruleQueries.findByIds(numbers) { id, _, annotation, _, content, _ ->
                SearchResult(id, annotation, content)
            }.executeAsList()
        }
    }

    override suspend fun queryRulesByContent(
            searchTerms: List<String>,
            limit: Int
    ): List<SearchResult> {
        require(limit > 0) { "Limit must be > 0, but it was $limit" }
        return withContext(Dispatchers.IO) {
            val query = searchTerms.joinToString(
                    separator = " ",
                    transform = { term -> "$term*" }
            )
            queryRulesByContent(query)
        }
    }

    private fun queryRulesByContent(query: String): List<SearchResult> {
        val sql = """
                SELECT
                	id,
                	annotation,
                	snippet(rule_content_fts, '', '', '...') AS snippet,
                	matchinfo(rule_content_fts, 'pclnx') AS matchinfo
                FROM rule_content_fts INNER JOIN rule_core ON rule_content_fts.rowid = rule_core.rowid
                WHERE rule_content_fts.content MATCH ?
                ORDER BY id ASC;
            """.trimIndent()
        val cursor = sqlDriver.executeQuery(0, sql, 1) {
            bindString(1, query)
        }
        return cursor.use {
            val result = mutableListOf<Pair<SearchResult, Double>>()
            while (cursor.next()) {
                val id = cursor.getLong(0)!!
                val annotation = cursor.getString(1)!!
                val snippet = cursor.getString(2)!!.replace("\n+".toRegex(), " ")
                val matchInfo = cursor.getBytes(3)!!
                result.add(SearchResult(id, annotation, snippet) to rank(matchInfo))
            }
            result.sortedByDescending { it.second }
                    .take(15)
                    .map { it.first }
        }
    }

    /**
     * Calculates tf-idf statistic given a BLOB value returned by SQLite matchinfo() function.
     * It assumes that FTS table contains only one column and matchinfo() function
     * was called with the following format string: 'pclnx'.
     *
     * @param matchInfo the BLOB value returned by SQLite matchinfo() function called with 'pclnx'
     * format string.
     *
     * @return the tf-idf statistic for the given matchinfo() BLOB value.
     */
    private fun rank(matchInfo: ByteArray): Double {
        val allData = matchInfo.asUnsignedIntArray()

        val termsInQuery = allData[0]
        val columns = allData[1]
        val wordsInDoc = allData[2]
        val docs = allData[3]

        var totalDocRank = 0.0
        val queryData = allData.slice(4..allData.lastIndex)
        check(columns == 1L)
        check(queryData.size / 3L == termsInQuery * columns)
        for (i in queryData.indices step 3) {
            val hitsInDoc = queryData[i]
            val docsWithHits = queryData[i + 2]
            totalDocRank += rank(hitsInDoc, wordsInDoc, docs, docsWithHits)
        }
        return totalDocRank
    }

    private fun ByteArray.asUnsignedIntArray(): LongArray {
        check(this.size % 4 == 0)
        val intBuffer = ByteBuffer.wrap(this)
                .order(ByteOrder.LITTLE_ENDIAN)
                .asIntBuffer()
        return LongArray(intBuffer.capacity()) { i ->
            intBuffer.get(i).toLong() and 0xFFFFFFFFL
        }
    }

    private fun rank(hitsInDoc: Long, wordsInDoc: Long, docs: Long, docsWithHits: Long): Double {
        return hitsInDoc / wordsInDoc.toDouble() * log2(1.0 + docs / docsWithHits)
    }
}