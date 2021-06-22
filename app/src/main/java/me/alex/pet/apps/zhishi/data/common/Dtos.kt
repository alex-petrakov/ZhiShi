package me.alex.pet.apps.zhishi.data.common

import com.squareup.moshi.JsonClass
import me.alex.pet.apps.zhishi.domain.common.*

@JsonClass(generateAdapter = true)
data class MarkupDto(
        val paragraphSpans: ParagraphSpansDto,
        val characterSpans: List<CharacterSpanDto>,
        val linkSpans: List<LinkSpanDto>
)

@JsonClass(generateAdapter = true)
data class ParagraphSpansDto(
        val indents: List<IndentSpanDto>,
        val styles: List<ParagraphStyleDto>
)

interface Sortable {
    val globalOrder: Int
}

@JsonClass(generateAdapter = true)
data class IndentSpanDto(
        val start: Int,
        val end: Int,
        val level: Int,
        val hangingText: String,
        override val globalOrder: Int
) : Sortable

@JsonClass(generateAdapter = true)
data class ParagraphStyleDto(
        val start: Int,
        val end: Int,
        val appearance: ParagraphAppearanceDto,
        override val globalOrder: Int
) : Sortable

enum class ParagraphAppearanceDto {
    QUOTE,
    FOOTNOTE
}

@JsonClass(generateAdapter = true)
data class CharacterSpanDto(
        val start: Int,
        val end: Int,
        val appearance: CharacterAppearanceDto
)

enum class CharacterAppearanceDto {
    EMPHASIS,
    STRONG_EMPHASIS,
    MISSPELL
}

@JsonClass(generateAdapter = true)
data class LinkSpanDto(
        val start: Int,
        val end: Int,
        val ruleId: Long
)

fun styledTextOf(content: String, markup: MarkupDto): StyledText {
    return StyledText(
            content,
            markup.toParagraphStyles(),
            markup.toCharacterStyles(),
            markup.toLinks()
    )
}

private fun MarkupDto.toLinks(): List<Link> {
    return linkSpans.map { Link(it.start, it.end, it.ruleId) }
}

private fun MarkupDto.toCharacterStyles(): List<CharacterSpan> {
    return characterSpans.map { CharacterSpan(it.start, it.end, it.appearance.toCharacterStyle()) }
}

private fun CharacterAppearanceDto.toCharacterStyle(): CharacterAppearance {
    return when (this) {
        CharacterAppearanceDto.EMPHASIS -> CharacterAppearance.EMPHASIS
        CharacterAppearanceDto.STRONG_EMPHASIS -> CharacterAppearance.STRONG_EMPHASIS
        CharacterAppearanceDto.MISSPELL -> CharacterAppearance.MISSPELL
    }
}

private fun MarkupDto.toParagraphStyles(): List<ParagraphSpan> {
    val allParagraphSpans = paragraphSpans.indents + paragraphSpans.styles
    return allParagraphSpans.sortedBy { it.globalOrder }.map { span ->
        when (span) {
            is IndentSpanDto -> ParagraphSpan.Indent(span.start, span.end, span.level, span.hangingText)
            is ParagraphStyleDto -> ParagraphSpan.Style(span.start, span.end, span.appearance.unwrap())
            else -> throw IllegalStateException()
        }
    }
}

private fun ParagraphAppearanceDto.unwrap(): ParagraphAppearance {
    return when (this) {
        ParagraphAppearanceDto.QUOTE -> ParagraphAppearance.QUOTE
        ParagraphAppearanceDto.FOOTNOTE -> ParagraphAppearance.FOOTNOTE
    }
}
