package me.alex.pet.apps.zhishi.presentation.common.styledtext

import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.widget.TextView
import me.alex.pet.apps.zhishi.domain.common.CharacterSpan
import me.alex.pet.apps.zhishi.domain.common.Link
import me.alex.pet.apps.zhishi.domain.common.ParagraphSpan
import me.alex.pet.apps.zhishi.domain.common.StyledText
import me.alex.pet.apps.zhishi.presentation.common.styledtext.elementconverters.ElementConverter

class StyledTextRenderer(
        private val paragraphStyleConverter: ElementConverter<ParagraphSpan>? = null,
        private val characterStyleConverter: ElementConverter<CharacterSpan>? = null,
        private val linkConverter: ElementConverter<Link>? = null
) {

    fun render(styledText: StyledText, textView: TextView) {
        val spanned = convertToSpanned(styledText, textView.paint)
        textView.text = spanned
    }

    fun convertToSpanned(styledText: StyledText, textPaint: TextPaint): Spanned {
        val spans = mutableListOf<PositionAwareSpan>()
        styledText.run {
            characterSpans.mapNotNullTo(spans) { characterStyleConverter?.convertToSpan(it, textPaint) }
            links.mapNotNullTo(spans) { linkConverter?.convertToSpan(it, textPaint) }
            // Indents must be rendered before paragraph spans in order for QuotationSpans to be
            // applied correctly
//            indents.mapNotNullTo(spans) { indentConverter?.convertToSpan(it, textPaint) }
            paragraphSpans.mapNotNullTo(spans) { paragraphStyleConverter?.convertToSpan(it, textPaint) }
        }

        return SpannableString(styledText.string).apply {
            setSpans(spans)
        }
    }
}

private fun Spannable.setSpans(spans: List<PositionAwareSpan>) {
    spans.forEach { setSpan(it.span, it.start, it.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) }
}