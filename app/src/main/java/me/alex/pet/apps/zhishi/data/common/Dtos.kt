package me.alex.pet.apps.zhishi.data.common

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import me.alex.pet.apps.zhishi.domain.common.*

@JsonClass(generateAdapter = true)
data class MarkupDto(
        @Json(name = "paragraphSpans") val paragraphSpans: List<ParagraphSpanDto>,
        @Json(name = "indentSpans") val indentSpans: List<IndentSpanDto>,
        @Json(name = "characterSpans") val characterSpans: List<CharacterSpanDto>,
        @Json(name = "linkSpans") val linkSpans: List<LinkSpanDto>
)

@JsonClass(generateAdapter = true)
data class ParagraphSpanDto(
        @Json(name = "start") val start: Int,
        @Json(name = "end") val end: Int,
        @Json(name = "style") val style: ParagraphSpanStyleDto
)

enum class ParagraphSpanStyleDto {
    QUOTE,
    FOOTNOTE
}

@JsonClass(generateAdapter = true)
data class IndentSpanDto(
        @Json(name = "start") val start: Int,
        @Json(name = "end") val end: Int,
        @Json(name = "level") val level: Int
)

@JsonClass(generateAdapter = true)
data class CharacterSpanDto(
        @Json(name = "start") val start: Int,
        @Json(name = "end") val end: Int,
        @Json(name = "style") val style: CharacterSpanStyleDto
)

enum class CharacterSpanStyleDto {
    EMPHASIS,
    STRONG_EMPHASIS,
    MISSPELL
}

@JsonClass(generateAdapter = true)
data class LinkSpanDto(
        @Json(name = "start") val start: Int,
        @Json(name = "end") val end: Int,
        @Json(name = "ruleId") val ruleId: Long
)

fun styledTextOf(content: String, markup: MarkupDto): StyledText {
    return StyledText(
            content,
            markup.toParagraphStyles(),
            markup.toIndents(),
            markup.toCharacterStyles(),
            markup.toLinks()
    )
}

private fun MarkupDto.toLinks(): List<Link> {
    return linkSpans.map { Link(it.start, it.end, it.ruleId) }
}

private fun MarkupDto.toCharacterStyles(): List<CharacterStyle> {
    return characterSpans.map { CharacterStyle(it.start, it.end, it.style.toCharacterStyle()) }
}

private fun CharacterSpanStyleDto.toCharacterStyle(): CharacterStyleType {
    return when (this) {
        CharacterSpanStyleDto.EMPHASIS -> CharacterStyleType.EMPHASIS
        CharacterSpanStyleDto.STRONG_EMPHASIS -> CharacterStyleType.STRONG_EMPHASIS
        CharacterSpanStyleDto.MISSPELL -> CharacterStyleType.MISSPELL
    }
}

private fun MarkupDto.toIndents(): List<Indent> {
    return indentSpans.map { Indent(it.start, it.end, it.level) }
}

private fun MarkupDto.toParagraphStyles(): List<ParagraphStyle> {
    return paragraphSpans.map { ParagraphStyle(it.start, it.end, it.style.toParagraphStyle()) }
}

private fun ParagraphSpanStyleDto.toParagraphStyle(): ParagraphStyleType {
    return when (this) {
        ParagraphSpanStyleDto.QUOTE -> ParagraphStyleType.QUOTE
        ParagraphSpanStyleDto.FOOTNOTE -> ParagraphStyleType.FOOTNOTE
    }
}
