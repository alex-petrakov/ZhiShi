package me.alex.pet.apps.zhishi.presentation.common.styledtext

import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import me.alex.pet.apps.zhishi.domain.*
import me.alex.pet.apps.zhishi.presentation.common.styledtext.elementconverters.ElementConverter

class StyledTextConverter(
        private val paragraphStyleConverter: ElementConverter<ParagraphStyle>? = null,
        private val indentConverter: ElementConverter<Indent>? = null,
        private val characterStyleConverter: ElementConverter<CharacterStyle>? = null,
        private val linkConverter: ElementConverter<Link>? = null
) {
    fun convertToSpanned(styledText: StyledText): Spanned {
        val spans = mutableListOf<PositionAwareSpan>()
        styledText.run {
            characterStyles.mapNotNullTo(spans) { characterStyleConverter?.convertToSpan(it) }
            links.mapNotNullTo(spans) { linkConverter?.convertToSpan(it) }
            // Indents must be rendered before paragraph spans in order for QuotationSpans to be
            // applied correctly
            indents.mapNotNullTo(spans) { indentConverter?.convertToSpan(it) }
            paragraphStyles.mapNotNullTo(spans) { paragraphStyleConverter?.convertToSpan(it) }
        }

        return SpannableString(styledText.string).apply {
            setSpans(spans)
        }
    }
}

private fun Spannable.setSpans(spans: List<PositionAwareSpan>) {
    spans.forEach { setSpan(it.span, it.start, it.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) }
}