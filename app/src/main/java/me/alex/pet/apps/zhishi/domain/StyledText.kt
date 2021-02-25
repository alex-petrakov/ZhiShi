package me.alex.pet.apps.zhishi.domain

data class StyledText(
        val string: String,
        val paragraphStyles: List<ParagraphStyle> = emptyList(),
        val indents: List<Indent> = emptyList(),
        val characterStyles: List<CharacterStyle> = emptyList(),
        val links: List<Link> = emptyList()
)

data class ParagraphStyle(val start: Int, val end: Int, val type: ParagraphStyleType)

enum class ParagraphStyleType {
    QUOTE,
    FOOTNOTE
}

data class Indent(val start: Int, val end: Int, val level: Int)

data class CharacterStyle(val start: Int, val end: Int, val type: CharacterStyleType)

enum class CharacterStyleType {
    EMPHASIS,
    STRONG_EMPHASIS,
    MISSPELL
}

data class Link(val start: Int, val end: Int, val ruleId: Long)