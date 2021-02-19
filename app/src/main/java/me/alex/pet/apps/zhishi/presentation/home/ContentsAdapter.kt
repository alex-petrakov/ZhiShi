package me.alex.pet.apps.zhishi.presentation.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.presentation.common.toSpannedString
import me.alex.pet.apps.zhishi.presentation.home.model.ContentsElement

class ContentsAdapter(
        private val onSectionClick: (Long) -> Unit
) : RecyclerView.Adapter<ContentsAdapter.Holder>() {

    var data: List<ContentsElement> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is ContentsElement.Part -> R.layout.item_generic_title
            is ContentsElement.Chapter -> R.layout.item_generic_subtitle
            is ContentsElement.Section -> R.layout.item_clickable_section
        }
    }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return Holder(view, onSectionClick)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(data[position])
    }


    class Holder(itemView: View, private val onSectionClick: (Long) -> Unit) : RecyclerView.ViewHolder(itemView) {

        private val textView = itemView.findViewById<TextView>(R.id.content_tv)

        fun bind(item: ContentsElement) {
            when (item) {
                is ContentsElement.Part -> bindPart(item)
                is ContentsElement.Chapter -> bindChapter(item)
                is ContentsElement.Section -> bindSection(item)
            }
        }

        private fun bindPart(part: ContentsElement.Part) {
            textView.text = part.name
            textView.setOnClickListener(null)
        }

        private fun bindChapter(chapter: ContentsElement.Chapter) {
            textView.text = chapter.name
            textView.setOnClickListener(null)
        }

        private fun bindSection(section: ContentsElement.Section) {
            textView.text = section.name.toSpannedString()
            textView.setOnClickListener { onSectionClick.invoke(section.id) }
        }
    }
}