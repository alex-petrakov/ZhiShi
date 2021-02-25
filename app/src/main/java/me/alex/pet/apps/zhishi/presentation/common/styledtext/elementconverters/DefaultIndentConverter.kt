package me.alex.pet.apps.zhishi.presentation.common.styledtext.elementconverters

import android.content.res.Resources
import android.text.style.LeadingMarginSpan
import androidx.annotation.Px
import me.alex.pet.apps.zhishi.domain.Indent
import me.alex.pet.apps.zhishi.presentation.common.styledtext.PositionAwareSpan
import kotlin.math.roundToInt

class DefaultIndentConverter(resources: Resources) : ElementConverter<Indent> {

    @Px
    private val indentStep: Int = 16.dp(resources)

    override fun convertToSpan(element: Indent): PositionAwareSpan? {
        return PositionAwareSpan(IndentSpan(element.level, indentStep), element.start, element.end)
    }
}

private fun Int.dp(resources: Resources): Int {
    return (this * resources.displayMetrics.density).roundToInt()
}

class IndentSpan(level: Int, @Px step: Int) : LeadingMarginSpan.Standard(step * level) {
    init {
        require(level in minLevel..maxLevel) { "Level must be in [$minLevel, $maxLevel], but it was $level" }
    }
}

private const val minLevel = 1
private const val maxLevel = 5