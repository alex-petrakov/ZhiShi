package me.alex.pet.apps.zhishi.presentation.contents

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.databinding.ItemClickableSectionBinding
import me.alex.pet.apps.zhishi.databinding.ItemGenericSubtitleBinding
import me.alex.pet.apps.zhishi.databinding.ItemGenericTitleBinding
import me.alex.pet.apps.zhishi.presentation.common.styledtext.StyledTextRenderer
import me.alex.pet.apps.zhishi.presentation.common.styledtext.spanrenderers.BasicCharSpanRenderer

class ContentsAdapter(
        private val onSectionClick: (Long) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: List<DisplayableItem> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val sectionStyledTextConverter = StyledTextRenderer(
            characterSpansRenderer = BasicCharSpanRenderer()
    )

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is DisplayableItem.Part -> R.layout.item_generic_title
            is DisplayableItem.Chapter -> R.layout.item_generic_subtitle
            is DisplayableItem.Section -> R.layout.item_clickable_section
        }
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.item_generic_title -> PartHolder(ItemGenericTitleBinding.bind(view))
            R.layout.item_generic_subtitle -> ChapterHolder(ItemGenericSubtitleBinding.bind(view))
            R.layout.item_clickable_section -> SectionHolder(
                    ItemClickableSectionBinding.bind(view),
                    sectionStyledTextConverter
            ) { adapterPosition -> onSectionClick((items[adapterPosition] as DisplayableItem.Section).id) }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is PartHolder -> holder.bind(item as DisplayableItem.Part)
            is ChapterHolder -> holder.bind(item as DisplayableItem.Chapter)
            is SectionHolder -> holder.bind(item as DisplayableItem.Section)
            else -> throw IllegalArgumentException("Unknown holder type: ${holder::class.java}")
        }
    }


    private class PartHolder(
            private val binding: ItemGenericTitleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DisplayableItem.Part) {
            binding.contentTv.text = item.name
        }
    }

    private class ChapterHolder(
            private val binding: ItemGenericSubtitleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DisplayableItem.Chapter) {
            binding.contentTv.text = item.name
        }
    }

    private class SectionHolder(
            private val binding: ItemClickableSectionBinding,
            private val styledTextRenderer: StyledTextRenderer,
            private val onClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener { onClick(adapterPosition) }
        }

        fun bind(item: DisplayableItem.Section) {
            styledTextRenderer.render(item.name, binding.contentTv)
        }
    }
}