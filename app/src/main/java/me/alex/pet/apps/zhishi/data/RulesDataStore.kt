package me.alex.pet.apps.zhishi.data

import com.squareup.moshi.Moshi
import me.alex.pet.apps.zhishi.ChapterQueries
import me.alex.pet.apps.zhishi.PartQueries
import me.alex.pet.apps.zhishi.RuleQueries
import me.alex.pet.apps.zhishi.SectionQueries
import me.alex.pet.apps.zhishi.domain.RulesRepository
import me.alex.pet.apps.zhishi.domain.StyledText
import me.alex.pet.apps.zhishi.domain.contents.Contents
import me.alex.pet.apps.zhishi.domain.contents.ContentsChapter
import me.alex.pet.apps.zhishi.domain.contents.ContentsPart
import me.alex.pet.apps.zhishi.domain.contents.ContentsSection
import me.alex.pet.apps.zhishi.domain.rules.Rule
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

    override suspend fun getContents(): Contents {
        return partQueries.transactionWithResult {
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

    override suspend fun getSection(sectionId: Long): Section? {
        return sectionQueries.transactionWithResult {
            sectionQueries.findById(sectionId) { id, _, name, markup ->
                val markupDto = markupAdapter.fromJson(markup)
                        ?: throw IllegalStateException("Unable to parse markup")
                Section(id, styledTextOf(name, markupDto), findRulesBySectionId(id))
            }.executeAsOneOrNull()
        }
    }

    private fun findRulesBySectionId(sectionId: Long): List<Rule> {
        return ruleQueries.findBySectionId(sectionId) { id, _, number, content, markup ->
            val markupDto = markupAdapter.fromJson(markup)
                    ?: throw IllegalStateException("Unable to parse markup")
            Rule(id, number, styledTextOf(content, markupDto))
        }.executeAsList()
    }

    override suspend fun getRule(ruleId: Long): Rule? {
        return ruleQueries.findById(ruleId) { id, _, number, content, markup ->
            val markupDto = markupAdapter.fromJson(markup)
                    ?: throw IllegalStateException("Unable to parse markup")
            Rule(id, number, styledTextOf(content, markupDto))
        }.executeAsOneOrNull()
    }

    override suspend fun query(searchTerms: List<String>, limit: Int): List<SearchResult> {
        require(searchTerms.isNotEmpty()) { "There must be at least one search term" }
        require(limit > 0) { "Limit must be > 0, but it was $limit" }
        val query = searchTerms.joinToString(separator = " OR ", transform = { term -> "$term*" })
        return ruleQueries.query(query, 30) { id, _, number, snippet, _ ->
            SearchResult(id, number, StyledText(snippet!!))
        }.executeAsList()
    }
}