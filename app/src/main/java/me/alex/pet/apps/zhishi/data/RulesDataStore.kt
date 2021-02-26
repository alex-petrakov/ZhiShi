package me.alex.pet.apps.zhishi.data

import me.alex.pet.apps.zhishi.domain.*
import me.alex.pet.apps.zhishi.domain.contents.Contents
import me.alex.pet.apps.zhishi.domain.contents.ContentsChapter
import me.alex.pet.apps.zhishi.domain.contents.ContentsPart
import me.alex.pet.apps.zhishi.domain.contents.ContentsSection
import me.alex.pet.apps.zhishi.domain.rules.Rule
import me.alex.pet.apps.zhishi.domain.rules.Section

class RulesDataStore : RulesRepository {

    private val vowelSections = listOf(
            ContentsSection(
                    0,
                    StyledText(
                            "Гласные после шипящих и ц",
                            characterStyles = listOf(
                                    CharacterStyle(24, 25, CharacterStyleType.EMPHASIS)
                            )
                    )
            ),
            ContentsSection(
                    1,
                    StyledText(
                            "Гласные ы и и после приставок",
                            characterStyles = listOf(
                                    CharacterStyle(8, 9, CharacterStyleType.EMPHASIS),
                                    CharacterStyle(12, 13, CharacterStyleType.EMPHASIS)
                            )
                    )
            ),
            ContentsSection(
                    2,
                    StyledText(
                            "Буква э",
                            characterStyles = listOf(
                                    CharacterStyle(6, 7, CharacterStyleType.EMPHASIS)
                            )
                    )
            ),
            ContentsSection(
                    3,
                    StyledText(
                            "Буква ё",
                            characterStyles = listOf(
                                    CharacterStyle(6, 7, CharacterStyleType.EMPHASIS)
                            )
                    )
            ),
            ContentsSection(4, StyledText("Общие правила правописание неударяемых гласных")),
            ContentsSection(5, StyledText("Неударяемые гласные в корнях слов")),
            ContentsSection(6, StyledText("Неударяемые гласные в приставках")),
            ContentsSection(7, StyledText("Неударяемые гласные в суффиксах")),
            ContentsSection(8, StyledText("Неударяемые соединительные глассные")),
            ContentsSection(9, StyledText("Гласные в некоторых неударяемых падежных окончаниях")),
            ContentsSection(10, StyledText("Неударяемые гласные в личных глагольных окончаниях"))
    )

    private val consonantSections = listOf(
            ContentsSection(11, StyledText("Общие правила")),
            ContentsSection(12, StyledText("Двойные согласные"))
    )


    private val chapters = listOf(
            ContentsChapter("Правописание гласных", vowelSections),
            ContentsChapter("Правописание согласных", consonantSections)
    )

    private val parts = listOf(
            ContentsPart("Орфография", chapters)
    )

    private val rules = listOf(
            Rule(
                    0,
                    1,
                    StyledText(
                            "После ж, ч, ш, щ не пишутся ю, я, ы, а пишутся у, а, и, например: чудо, щука, час, роща, жир, шить.\n\nБуквы ю и я допускаются после этих согласных только в иноязычных словах (преимущественно французских), например: жюри, парашют (в том числе — в именах собственных, например: Сен-Жюст), а также в сложносокращённых словах и буквенных аббревиатурах, в которых, по общему правилу, допускаются любые сочетания букв (см. § 110).",
                            characterStyles = listOf(
                                    CharacterStyle(6, 7, CharacterStyleType.EMPHASIS),
                                    CharacterStyle(0, 5, CharacterStyleType.STRONG_EMPHASIS),
                                    CharacterStyle(9, 15, CharacterStyleType.MISSPELL)
                            ),
                            links = listOf(
                                    Link(15, 20, 3)
                            ),
                            paragraphStyles = listOf(
                                    ParagraphStyle(0, 99, ParagraphStyleType.QUOTE)
                            ),
                            indents = listOf(
                                    Indent(0, 99, 1)
                            )
                    )
            ),
            Rule(
                    1,
                    2,
                    StyledText(
                            "После ц буква ы пишется в окончаниях и в суффиксе -ын, например: птицы, о́вцы и овцы́, огурцы, белолицый, сестрицын, лисицын, а также в словах цыган, цыплёнок, на цыпочках, цыц (междометие) и в других словах того же корня.\n\nВ остальных случаях после ц пишется всегда и, например: станция, цибик, циновка, цимбалы, цинк, медицина. "
                    )
            ),
            Rule(
                    2,
                    3,
                    StyledText(
                            "После ц буквы ю и я допускаются только в иноязычных именах собственных, например Цюрих, Свенцяны."
                    )
            ),
            Rule(
                    3,
                    4,
                    StyledText("При сочетании приставки, оканчивающейся на согласный, с корнем или с другой приставкой, которые начинаются с и, пишется, согласно с произношением, по общему правилу ы, например: розыск, предыдущий, возыметь, изымать (но: взимать, где произносится и), подытожить, безыскусственный, сызнова, безыдейный, безынициативный, безынтересный, сымпровизировать, надындивидуальный, предыстория.")
            )
    ).associateBy { it.id }

    private val sections = mapOf(
            0L to Section(
                    0,
                    StyledText("Гласные после шипящих и ц", characterStyles = listOf(CharacterStyle(24, 25, CharacterStyleType.EMPHASIS))),
                    rules.filter { it.key in 0L..2L }.values.toList()
            ),
            1L to Section(
                    1,
                    StyledText("Гласные ы и и после приставок", characterStyles = listOf(CharacterStyle(8, 9, CharacterStyleType.EMPHASIS), CharacterStyle(12, 13, CharacterStyleType.EMPHASIS))),
                    rules.filter { it.key == 3L }.values.toList()
            )
    )


    override suspend fun getContents(): Contents {
        return Contents(parts)
    }

    override suspend fun getSection(sectionId: Long): Section? {
        return sections[sectionId]
    }

    override suspend fun getRule(ruleId: Long): Rule? {
        return rules[ruleId]
    }
}