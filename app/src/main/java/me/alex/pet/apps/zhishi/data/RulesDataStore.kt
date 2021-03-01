package me.alex.pet.apps.zhishi.data

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

class RulesDataStore(
        private val partQueries: PartQueries,
        private val chapterQueries: ChapterQueries,
        private val sectionQueries: SectionQueries,
        private val ruleQueries: RuleQueries
) : RulesRepository {

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
            ContentsSection(id, StyledText(name)) // TODO: Parse markup
        }.executeAsList()
    }

    override suspend fun getSection(sectionId: Long): Section? {
        return sectionQueries.transactionWithResult {
            sectionQueries.findById(sectionId) { id, chapterId, name, markup ->
                Section(id, StyledText(name), findRulesBySectionId(id)) // TODO: Parse markup
            }.executeAsOneOrNull()
        }
    }

    private fun findRulesBySectionId(sectionId: Long): List<Rule> {
        return ruleQueries.findBySectionId(sectionId) { id, _, number, content, markup ->
            Rule(id, number, StyledText(content)) // TODO: Parse markup
        }.executeAsList()
    }

    override suspend fun getRule(ruleId: Long): Rule? {
        return ruleQueries.findById(ruleId) { id, _, number, content, markup ->
            Rule(id, number, StyledText(content)) // TODO: Parse markup
        }.executeAsOneOrNull()
    }
}