package me.alex.pet.apps.zhishi.data.search

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.alex.pet.apps.zhishi.RuleQueries
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
            ruleQueries.findByContent(query, limit.toLong()) { id, annotation, snippet ->
                SearchResult(id, annotation, snippet!!.replace("\n+".toRegex(), " "))
            }.executeAsList()
        }
    }
}