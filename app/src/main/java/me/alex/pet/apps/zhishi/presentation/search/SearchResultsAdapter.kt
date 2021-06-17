package me.alex.pet.apps.zhishi.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.databinding.ItemRuleSearchResultBinding
import me.alex.pet.apps.zhishi.presentation.common.styledtext.StyledTextRenderer
import me.alex.pet.apps.zhishi.presentation.common.styledtext.spanrenderers.CharSpanRenderer
import me.alex.pet.apps.zhishi.presentation.common.styledtext.spanrenderers.ParagraphSpanRenderer

class SearchResultsAdapter(
        private val onRuleClick: (Long) -> Unit
) : RecyclerView.Adapter<SearchResultsAdapter.Holder>() {

    var items: List<SearchResultItem>
        set(value) {
            listDiffer.submitList(value)
        }
        get() = listDiffer.currentList

    private val diffCallback = object : DiffUtil.ItemCallback<SearchResultItem>() {
        override fun areItemsTheSame(oldItem: SearchResultItem, newItem: SearchResultItem): Boolean {
            return oldItem.ruleId == newItem.ruleId
        }

        override fun areContentsTheSame(oldItem: SearchResultItem, newItem: SearchResultItem): Boolean {
            return oldItem == newItem
        }
    }

    private val listDiffer = AsyncListDiffer(this, diffCallback)

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemRuleSearchResultBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        )
        return Holder(binding) { adapterPosition -> onRuleClick(items[adapterPosition].ruleId) }
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position])
    }

    class Holder(
            private val binding: ItemRuleSearchResultBinding,
            private val onClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val styledTextRenderer = StyledTextRenderer(
                paragraphSpansRenderer = ParagraphSpanRenderer(theme, binding.contentTv.paint),
                characterSpansRenderer = CharSpanRenderer(theme)
        )

        private val context get() = binding.root.context

        private val theme get() = context.theme

        init {
            binding.root.setOnClickListener { onClick(adapterPosition) }
        }

        fun bind(item: SearchResultItem) = with(binding) {
            numberTv.text = context.getString(R.string.rule_rule_number, item.ruleId)
            styledTextRenderer.render(item.snippet, contentTv)
        }
    }
}