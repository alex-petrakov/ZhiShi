package me.alex.pet.apps.zhishi.domain.common

data class ParagraphSpan(
        val start: Int,
        val end: Int,
        val appearance: ParagraphAppearance,
        val indent: Indent
) {
    init {
        require(start >= 0)
        require(end > start)
    }
}

data class Indent(val outer: Int, val inner: Int, val hangingText: String) {
    init {
        require(outer >= 0)
        require(inner >= 0)
    }
}

enum class ParagraphAppearance {
    NORMAL,
    FOOTNOTE,
    QUOTE,
    FOOTNOTE_QUOTE
}