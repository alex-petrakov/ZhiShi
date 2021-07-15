package me.alex.pet.apps.zhishi.data.common

import com.squareup.moshi.JsonClass
import me.alex.pet.apps.zhishi.domain.common.*

@JsonClass(generateAdapter = true)
data class MarkupDto(
        val paragraphSpans: List<ParagraphSpanDto>,
        val characterSpans: List<CharacterSpanDto>,
        val linkSpans: List<LinkSpanDto>
)

@JsonClass(generateAdapter = true)
data class ParagraphSpanDto(
        val start: Int,
        val end: Int,
        val appearance: ParagraphAppearanceDto,
        val indent: IndentDto
)

@JsonClass(generateAdapter = true)
data class IndentDto(
        val outer: Int,
        val inner: Int,
        val hangingText: String
)

enum class ParagraphAppearanceDto {
    NORMAL,
    FOOTNOTE,
    QUOTE,
    FOOTNOTE_QUOTE
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
    return paragraphSpans.map { it.unwrap() }
}

private fun ParagraphSpanDto.unwrap(): ParagraphSpan {
    return ParagraphSpan(start, end, appearance.unwrap(), indent.unwrap())
}

private fun ParagraphAppearanceDto.unwrap(): ParagraphAppearance {
    return when (this) {
        ParagraphAppearanceDto.NORMAL -> ParagraphAppearance.NORMAL
        ParagraphAppearanceDto.FOOTNOTE -> ParagraphAppearance.FOOTNOTE
        ParagraphAppearanceDto.QUOTE -> ParagraphAppearance.QUOTE
        ParagraphAppearanceDto.FOOTNOTE_QUOTE -> ParagraphAppearance.FOOTNOTE_QUOTE
    }
}

private fun IndentDto.unwrap(): Indent {
    return Indent(outer, inner, hangingText)
}
