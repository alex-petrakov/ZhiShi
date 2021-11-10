package me.alex.pet.apps.zhishi.data.sections

import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.alex.pet.apps.zhishi.RuleQueries
import me.alex.pet.apps.zhishi.SectionQueries
import me.alex.pet.apps.zhishi.data.common.MarkupDto
import me.alex.pet.apps.zhishi.data.common.styledTextOf
import me.alex.pet.apps.zhishi.domain.sections.Rule
import me.alex.pet.apps.zhishi.domain.sections.Section
import me.alex.pet.apps.zhishi.domain.sections.SectionsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SectionsDataStore @Inject constructor(
    private val sectionQueries: SectionQueries,
    private val ruleQueries: RuleQueries,
    moshi: Moshi
) : SectionsRepository {

    private val markupAdapter = moshi.adapter(MarkupDto::class.java)

    override suspend fun getSection(sectionId: Long): Section? = withContext(Dispatchers.IO) {
        sectionQueries.transactionWithResult {
            sectionQueries.findById(sectionId) { id, _, name, markup ->
                val markupDto = markupAdapter.fromJson(markup)
                    ?: throw IllegalStateException("Unable to parse markup")
                Section(id, styledTextOf(name, markupDto), findRulesBySectionId(id))
            }.executeAsOneOrNull()
        }
    }

    private fun findRulesBySectionId(sectionId: Long): List<Rule> {
        return ruleQueries.findIdAndAnnotationBySectionId(sectionId) { id, annotation, annotationMarkup ->
            val annotationMarkupDto = markupAdapter.fromJson(annotationMarkup)
                    ?: throw IllegalStateException("Unable to parse markup")
            Rule(id, styledTextOf(annotation, annotationMarkupDto))
        }.executeAsList()
    }
}