package me.alex.pet.apps.zhishi.data.common

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import me.alex.pet.apps.zhishi.domain.common.*

@JsonClass(generateAdapter = true)
data class MarkupDto(
        @Json(name = "ps") val paragraphSpans: List<ParagraphSpanDto>,
        @Json(name = "cs") val characterSpans: List<CharacterSpanDto>,
        @Json(name = "ls") val linkSpans: List<LinkSpanDto>
)

@JsonClass(generateAdapter = true)
data class ParagraphSpanDto(
        @Json(name = "s") val start: Int,
        @Json(name = "e") val end: Int,
        @Json(name = "a") val appearance: ParagraphAppearanceDto,
        @Json(name = "i") val indent: IndentDto
)

@JsonClass(generateAdapter = true)
data class IndentDto(
        @Json(name = "o") val outer: Int,
        @Json(name = "i") val inner: Int,
        @Json(name = "ht") val hangingText: String
)

@JsonClass(generateAdapter = false)
enum class ParagraphAppearanceDto {
    NORMAL,
    FOOTNOTE,
    QUOTE,
    FOOTNOTE_QUOTE
}

@JsonClass(generateAdapter = true)
data class CharacterSpanDto(
        @Json(name = "s") val start: Int,
        @Json(name = "e") val end: Int,
        @Json(name = "a") val appearance: CharacterAppearanceDto
)

@JsonClass(generateAdapter = false)
enum class CharacterAppearanceDto {
    EMPHASIS,
    STRONG_EMPHASIS,
    MISSPELL
}

@JsonClass(generateAdapter = true)
data class LinkSpanDto(
        @Json(name = "s") val start: Int,
        @Json(name = "e") val end: Int,
        @Json(name = "ri") val ruleId: Long
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
