package me.alex.pet.apps.zhishi.data.rules

import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.alex.pet.apps.zhishi.RuleQueries
import me.alex.pet.apps.zhishi.data.common.MarkupDto
import me.alex.pet.apps.zhishi.data.common.styledTextOf
import me.alex.pet.apps.zhishi.domain.common.StyledText
import me.alex.pet.apps.zhishi.domain.rules.Rule
import me.alex.pet.apps.zhishi.domain.rules.RulesRepository
import me.alex.pet.apps.zhishi.domain.search.SearchResult

class RulesDataStore(
        private val ruleQueries: RuleQueries,
        moshi: Moshi
) : RulesRepository {

    private val markupAdapter = moshi.adapter(MarkupDto::class.java)

    override suspend fun getRule(ruleId: Long): Rule? = withContext(Dispatchers.IO) {
        ruleQueries.findById(ruleId) { id, _, annotation, annotationMarkup, content, contentMarkup ->
            val annotationMarkupDto = markupAdapter.fromJson(annotationMarkup)
                    ?: throw IllegalStateException("Unable to parse markup")
            val contentMarkupDto = markupAdapter.fromJson(contentMarkup)
                    ?: throw IllegalStateException("Unable to parse markup")
            Rule(id, styledTextOf(annotation, annotationMarkupDto), styledTextOf(content, contentMarkupDto))
        }.executeAsOneOrNull()
    }

    override suspend fun queryRulesById(numbers: List<Long>): List<SearchResult> = withContext(Dispatchers.IO) {
        ruleQueries.queryById(numbers) { id, _, _, _, content, _ ->
            SearchResult(id, StyledText(content))
        }.executeAsList()
    }

    override suspend fun queryRulesByContent(searchTerms: List<String>, limit: Int): List<SearchResult> = withContext(Dispatchers.IO) {
        require(limit > 0) { "Limit must be > 0, but it was $limit" }
        val query = searchTerms.joinToString(separator = " OR ", transform = { term -> "$term*" })
        ruleQueries.queryByContent(query, 30) { id, _, snippet, _ ->
            SearchResult(id, StyledText(snippet!!))
        }.executeAsList()
    }
}