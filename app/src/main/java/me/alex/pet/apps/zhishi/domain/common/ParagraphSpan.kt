package me.alex.pet.apps.zhishi.domain.common

sealed class ParagraphSpan(val start: Int, val end: Int) {

    init {
        require(start >= 0)
        require(end > start)
    }

    class Indent(start: Int, end: Int, val level: Int) : ParagraphSpan(start, end) {
        init {
            require(level > 0)
        }
    }

    class HangingIndent(start: Int, end: Int, val hangingText: String) : ParagraphSpan(start, end)

    class Style(start: Int, end: Int, val appearance: ParagraphAppearance) : ParagraphSpan(start, end)
}

enum class ParagraphAppearance {
    QUOTE,
    FOOTNOTE
}