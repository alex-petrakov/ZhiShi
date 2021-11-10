package me.alex.pet.apps.zhishi.data.rules

import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.alex.pet.apps.zhishi.RuleQueries
import me.alex.pet.apps.zhishi.data.common.MarkupDto
import me.alex.pet.apps.zhishi.data.common.styledTextOf
import me.alex.pet.apps.zhishi.domain.rules.Rule
import me.alex.pet.apps.zhishi.domain.rules.RulesRepository
import me.alex.pet.apps.zhishi.domain.rules.Section
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RulesDataStore @Inject constructor(
    private val ruleQueries: RuleQueries,
    moshi: Moshi
) : RulesRepository {

    private val markupAdapter = moshi.adapter(MarkupDto::class.java)

    override suspend fun getRule(ruleId: Long): Rule? = withContext(Dispatchers.IO) {
        ruleQueries.findById(ruleId) { id, content, contentMarkup, sectionId, sectionName, sectionMarkup ->
            val contentMarkupDto = markupAdapter.fromJson(contentMarkup)
                ?: throw IllegalStateException("Unable to parse markup")
            val sectionNameMarkupDto = markupAdapter.fromJson(sectionMarkup)
                ?: throw IllegalStateException("Unable to parse markup")
            val section = Section(sectionId, styledTextOf(sectionName, sectionNameMarkupDto))
            Rule(id, styledTextOf(content, contentMarkupDto), section)
        }.executeAsOneOrNull()
    }

    override suspend fun getIdsOfRulesInSameSection(ruleId: Long): LongRange {
        return withContext(Dispatchers.IO) {
            val ids = ruleQueries.findIdsOfRulesInSameSection(ruleId).executeAsList()
            ids.first()..ids.last()
        }
    }
}