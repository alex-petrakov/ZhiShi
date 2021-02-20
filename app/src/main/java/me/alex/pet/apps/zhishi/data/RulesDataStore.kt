package me.alex.pet.apps.zhishi.data

import me.alex.pet.apps.zhishi.domain.RulesRepository
import me.alex.pet.apps.zhishi.domain.StyledText
import me.alex.pet.apps.zhishi.domain.contents.Contents
import me.alex.pet.apps.zhishi.domain.contents.ContentsChapter
import me.alex.pet.apps.zhishi.domain.contents.ContentsPart
import me.alex.pet.apps.zhishi.domain.contents.ContentsSection

class RulesDataStore : RulesRepository {

    private val vowelSections = listOf(
            ContentsSection(1, StyledText("Гласные после шипящих и ц")),
            ContentsSection(2, StyledText("Гласные ы и и после приставок")),
            ContentsSection(3, StyledText("Буква э")),
            ContentsSection(4, StyledText("Буква ё")),
            ContentsSection(5, StyledText("Общие правила правописание неударяемых гласных")),
            ContentsSection(6, StyledText("Неударяемые гласные в корнях слов")),
            ContentsSection(7, StyledText("Неударяемые гласные в приставках")),
            ContentsSection(8, StyledText("Неударяемые гласные в суффиксах")),
            ContentsSection(9, StyledText("Неударяемые соединительные глассные")),
            ContentsSection(10, StyledText("Гласные в некоторых неударяемых падежных окончаниях")),
            ContentsSection(11, StyledText("Неударяемые гласные в личных глагольных окончаниях"))
    )

    private val consonantSections = listOf(
            ContentsSection(12, StyledText("Общие правила")),
            ContentsSection(13, StyledText("Двойные согласные"))
    )


    private val chapters = listOf(
            ContentsChapter("Правописание гласных", vowelSections),
            ContentsChapter("Правописание согласных", consonantSections)
    )

    private val parts = listOf(
            ContentsPart("Орфография", chapters)
    )


    override suspend fun getContents(): Contents {
        return Contents(parts)
    }
}