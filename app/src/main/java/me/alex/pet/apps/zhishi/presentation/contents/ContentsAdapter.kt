package me.alex.pet.apps.zhishi.presentation.contents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.presentation.common.styledtext.StyledTextConverter
import me.alex.pet.apps.zhishi.presentation.common.styledtext.elementconverters.BasicCharStyleConverter

class ContentsAdapter(
        private val onSectionClick: (Long) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: List<ContentsElement> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val sectionStyledTextConverter = StyledTextConverter(
            characterStyleConverter = BasicCharStyleConverter()
    )

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
            R.layout.item_clickable_section -> SectionHolder(view, sectionStyledTextConverter, onSectionClick)
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


    private class PartHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textView = itemView.findViewById<TextView>(R.id.content_tv)

        fun bind(item: ContentsElement.Part) {
            textView.text = item.name
        }
    }

    private class ChapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textView = itemView.findViewById<TextView>(R.id.content_tv)

        fun bind(item: ContentsElement.Chapter) {
            textView.text = item.name
        }
    }

    private class SectionHolder(
            itemView: View,
            private val styledTextConverter: StyledTextConverter,
            private val onSectionClick: (Long) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val textView = itemView.findViewById<TextView>(R.id.content_tv)

        fun bind(item: ContentsElement.Section) {
            textView.setOnClickListener { onSectionClick.invoke(item.id) }
            textView.text = styledTextConverter.convertToSpanned(item.name)
        }
    }
}