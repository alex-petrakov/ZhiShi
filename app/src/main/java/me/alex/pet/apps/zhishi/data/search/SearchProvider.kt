package me.alex.pet.apps.zhishi.data.search

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.alex.pet.apps.zhishi.RuleQueries
import me.alex.pet.apps.zhishi.domain.common.StyledText
import me.alex.pet.apps.zhishi.domain.search.SearchRepository
import me.alex.pet.apps.zhishi.domain.search.SearchResult

class SearchProvider(private val ruleQueries: RuleQueries) : SearchRepository {

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
            ruleQueries.findByIds(numbers) { id, _, _, _, content, _ ->
                SearchResult(id, StyledText(content))
            }.executeAsList()
        }
    }

    override suspend fun queryRulesByContent(
            searchTerms: List<String>,
            limit: Int
    ): List<SearchResult> {
        return withContext(Dispatchers.IO) {
            require(limit > 0) { "Limit must be > 0, but it was $limit" }
            val query = searchTerms.joinToString(separator = " OR ", transform = { term -> "$term*" })
            ruleQueries.findByContent(query, 30) { id, _, snippet, _ ->
                SearchResult(id, StyledText(snippet!!.replace("\n+".toRegex(), " ")))
            }.executeAsList()
        }
    }
}