package me.alex.pet.apps.zhishi.domain.common

sealed class ParagraphSpan(val start: Int, val end: Int) {

    init {
        require(start >= 0)
        require(end > start)
    }

    class Indent(start: Int, end: Int, val level: Int, val hangingText: String) : ParagraphSpan(start, end) {
        init {
            require(level >= 0)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Indent) return false
            if (!super.equals(other)) return false

            if (level != other.level) return false
            if (hangingText != other.hangingText) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + level
            result = 31 * result + hangingText.hashCode()
            return result
        }

        override fun toString(): String {
            return "Indent(start=$start, end=$end, level=$level, hangingText='$hangingText')"
        }
    }

    class Style(start: Int, end: Int, val appearance: ParagraphAppearance) : ParagraphSpan(start, end) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Style) return false
            if (!super.equals(other)) return false

            if (appearance != other.appearance) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + appearance.hashCode()
            return result
        }

        override fun toString(): String {
            return "Style(start=$start, end=$end, appearance=$appearance)"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ParagraphSpan) return false

        if (start != other.start) return false
        if (end != other.end) return false

        return true
    }

    override fun hashCode(): Int {
        var result = start
        result = 31 * result + end
        return result
    }
}

enum class ParagraphAppearance {
    QUOTE,
    FOOTNOTE
}