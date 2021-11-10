package me.alex.pet.apps.zhishi.data.contents

import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.alex.pet.apps.zhishi.ChapterQueries
import me.alex.pet.apps.zhishi.PartQueries
import me.alex.pet.apps.zhishi.SectionQueries
import me.alex.pet.apps.zhishi.data.common.MarkupDto
import me.alex.pet.apps.zhishi.data.common.styledTextOf
import me.alex.pet.apps.zhishi.domain.contents.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentsDataStore @Inject constructor(
    private val partQueries: PartQueries,
    private val chapterQueries: ChapterQueries,
    private val sectionQueries: SectionQueries,
    moshi: Moshi
) : ContentsRepository {

    private val markupAdapter = moshi.adapter(MarkupDto::class.java)

    override suspend fun getContents(): Contents = withContext(Dispatchers.IO) {
        partQueries.transactionWithResult {
            val parts = partQueries.getAll { id, name ->
                PartNode(name, getChaptersByPartId(id))
            }.executeAsList()
            Contents(parts)
        }
    }

    private fun getChaptersByPartId(partId: Long): List<ChapterNode> {
        return chapterQueries.findByPartId(partId) { id, _, name ->
            ChapterNode(name, getSectionsByChapterId(id))
        }.executeAsList()
    }

    private fun getSectionsByChapterId(chapterId: Long): List<SectionNode> {
        return sectionQueries.findByChapterId(chapterId) { id, _, name, markup ->
            val markupDto = markupAdapter.fromJson(markup)
                    ?: throw IllegalStateException("Unable to parse markup")
            SectionNode(id, styledTextOf(name, markupDto))
        }.executeAsList()
    }
}