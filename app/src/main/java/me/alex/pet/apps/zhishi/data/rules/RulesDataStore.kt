package me.alex.pet.apps.zhishi.data.rules

import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.alex.pet.apps.zhishi.RuleQueries
import me.alex.pet.apps.zhishi.data.common.MarkupDto
import me.alex.pet.apps.zhishi.data.common.styledTextOf
import me.alex.pet.apps.zhishi.domain.rules.Rule
import me.alex.pet.apps.zhishi.domain.rules.RulesRepository

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

    override suspend fun getIdsOfRulesInSameSection(ruleId: Long): LongRange {
        return withContext(Dispatchers.IO) {
            val ids = ruleQueries.findIdsOfRulesInSameSection(ruleId).executeAsList()
            ids.first()..ids.last()
        }
    }
}