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
import me.alex.pet.apps.zhishi.presentation.common.styledtext.spanrenderers.SpanRenderer

class StyledTextRenderer(
        private val paragraphSpansRenderer: SpanRenderer<ParagraphSpan>? = null,
        private val characterSpansRenderer: SpanRenderer<CharacterSpan>? = null,
        private val linksRenderer: SpanRenderer<Link>? = null
) {

    fun render(styledText: StyledText, textView: TextView) {
        val spanned = convertToSpanned(styledText, textView.paint)
        textView.text = spanned
    }

    fun convertToSpanned(styledText: StyledText, textPaint: TextPaint): Spanned {
        val characterSpans = characterSpansRenderer?.convertToSpans(
                styledText.characterSpans,
                textPaint
        ) ?: emptyList()
        val linkSpans = linksRenderer?.convertToSpans(styledText.links, textPaint) ?: emptyList()
        val paragraphSpans = paragraphSpansRenderer?.convertToSpans(
                styledText.paragraphSpans,
                textPaint
        ) ?: emptyList()
        return SpannableString(styledText.string).apply {
            setSpans(characterSpans + linkSpans + paragraphSpans)
        }
    }
}

private fun Spannable.setSpans(spans: List<PositionAwareSpan>) {
    spans.forEach { setSpan(it.span, it.start, it.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) }
}