package me.alex.pet.apps.zhishi.presentation.home

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.domain.CharacterStyleType
import me.alex.pet.apps.zhishi.domain.StyledText

class ContentsAdapter(
        private val onSectionClick: (Long) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: List<ContentsElement> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ContentsElement.Part -> R.layout.item_generic_title
            is ContentsElement.Chapter -> R.layout.item_generic_subtitle
            is ContentsElement.Section -> R.layout.item_clickable_section
        }
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.item_generic_title -> PartHolder(view)
            R.layout.item_generic_subtitle -> ChapterHolder(view)
            R.layout.item_clickable_section -> SectionHolder(view, onSectionClick)
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is PartHolder -> holder.bind(item as ContentsElement.Part)
            is ChapterHolder -> holder.bind(item as ContentsElement.Chapter)
            is SectionHolder -> holder.bind(item as ContentsElement.Section)
            else -> throw IllegalArgumentException("Unknown holder type: ${holder::class.java}")
        }
    }


    class PartHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textView = itemView.findViewById<TextView>(R.id.content_tv)

        fun bind(item: ContentsElement.Part) {
            textView.text = item.name
        }
    }

    class ChapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textView = itemView.findViewById<TextView>(R.id.content_tv)

        fun bind(item: ContentsElement.Chapter) {
            textView.text = item.name
        }
    }

    class SectionHolder(
            itemView: View,
            private val onSectionClick: (Long) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val textView = itemView.findViewById<TextView>(R.id.content_tv)

        fun bind(item: ContentsElement.Section) {
            textView.text = item.name.toSpannedString()
            textView.setOnClickListener { onSectionClick.invoke(item.id) }
        }
    }
}


private fun StyledText.toSpannedString(): Spanned {
    val spansAndPositions = characterStyles.mapNotNull { style ->
        when (val span = style.type.toSpan()) {
            null -> null
            else -> Triple(span, style.start, style.end)
        }
    }
    val spannable = SpannableString(string)
    spansAndPositions.forEach { (span, start, end) ->
        spannable.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return spannable
}

private fun CharacterStyleType.toSpan(): CharacterStyle? {
    return when (this) {
        CharacterStyleType.EMPHASIS -> StyleSpan(Typeface.ITALIC)
        CharacterStyleType.STRONG_EMPHASIS -> StyleSpan(Typeface.BOLD)
        else -> null
    }
}