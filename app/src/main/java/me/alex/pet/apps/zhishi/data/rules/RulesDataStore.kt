package me.alex.pet.apps.zhishi.data.rules

import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.alex.pet.apps.zhishi.ChapterQueries
import me.alex.pet.apps.zhishi.PartQueries
import me.alex.pet.apps.zhishi.RuleQueries
import me.alex.pet.apps.zhishi.SectionQueries
import me.alex.pet.apps.zhishi.domain.common.StyledText
import me.alex.pet.apps.zhishi.domain.contents.Contents
import me.alex.pet.apps.zhishi.domain.contents.ContentsChapter
import me.alex.pet.apps.zhishi.domain.contents.ContentsPart
import me.alex.pet.apps.zhishi.domain.contents.ContentsSection
import me.alex.pet.apps.zhishi.domain.rules.Rule
import me.alex.pet.apps.zhishi.domain.rules.RulesRepository
import me.alex.pet.apps.zhishi.domain.rules.Section
import me.alex.pet.apps.zhishi.domain.search.SearchResult

class RulesDataStore(
        private val partQueries: PartQueries,
        private val chapterQueries: ChapterQueries,
        private val sectionQueries: SectionQueries,
        private val ruleQueries: RuleQueries,
        moshi: Moshi
) : RulesRepository {

    private val markupAdapter = moshi.adapter(MarkupDto::class.java)

    override suspend fun getContents(): Contents = withContext(Dispatchers.IO) {
        partQueries.transactionWithResult {
            val parts = partQueries.getAll { id, name ->
                ContentsPart(name, findContentsChaptersByPartId(id))
            }.executeAsList()
            Contents(parts)
        }
    }

    private fun findContentsChaptersByPartId(partId: Long): List<ContentsChapter> {
        return chapterQueries.findByPartId(partId) { id, _, name ->
            ContentsChapter(name, findContentsSectionsByChapterId(id))
        }.executeAsList()
    }

    private fun findContentsSectionsByChapterId(chapterId: Long): List<ContentsSection> {
        return sectionQueries.findByChapterId(chapterId) { id, _, name, markup ->
            val markupDto = markupAdapter.fromJson(markup)
                    ?: throw IllegalStateException("Unable to parse markup")
            ContentsSection(id, styledTextOf(name, markupDto))
        }.executeAsList()
    }

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
        return ruleQueries.findBySectionId(sectionId) { id, _, number, annotation, annotationMarkup, content, contentMarkup ->
            val annotationMarkupDto = markupAdapter.fromJson(annotationMarkup)
                    ?: throw IllegalStateException("Unable to parse markup")
            val contentMarkupDto = markupAdapter.fromJson(contentMarkup)
                    ?: throw IllegalStateException("Unable to parse markup")
            Rule(id, number, styledTextOf(annotation, annotationMarkupDto), styledTextOf(content, contentMarkupDto))
        }.executeAsList()
    }

    override suspend fun getRule(ruleId: Long): Rule? = withContext(Dispatchers.IO) {
        ruleQueries.findById(ruleId) { id, _, number, annotation, annotationMarkup, content, contentMarkup ->
            val annotationMarkupDto = markupAdapter.fromJson(annotationMarkup)
                    ?: throw IllegalStateException("Unable to parse markup")
            val contentMarkupDto = markupAdapter.fromJson(contentMarkup)
                    ?: throw IllegalStateException("Unable to parse markup")
            Rule(id, number, styledTextOf(annotation, annotationMarkupDto), styledTextOf(content, contentMarkupDto))
        }.executeAsOneOrNull()
    }

    override suspend fun queryRulesByNumber(numbers: List<Int>): List<SearchResult> = withContext(Dispatchers.IO) {
        ruleQueries.queryByNumber(numbers) { id, _, number, _, _, content, _ ->
            SearchResult(id, number, StyledText(content))
        }.executeAsList()
    }

    override suspend fun queryRulesByContent(searchTerms: List<String>, limit: Int): List<SearchResult> = withContext(Dispatchers.IO) {
        require(limit > 0) { "Limit must be > 0, but it was $limit" }
        val query = searchTerms.joinToString(separator = " OR ", transform = { term -> "$term*" })
        ruleQueries.queryByContent(query, 30) { id, _, number, snippet, _ ->
            SearchResult(id, number, StyledText(snippet!!))
        }.executeAsList()
    }
}