package me.alex.pet.apps.zhishi.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.databinding.ItemRuleSearchResultBinding
import me.alex.pet.apps.zhishi.presentation.common.extensions.withExistingAdapterPosition

class SearchResultsAdapter(
        private val onRuleClick: (Long) -> Unit
) : ListAdapter<SearchResultItem, SearchResultsAdapter.Holder>(SearchResultItem.DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemRuleSearchResultBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        )
        return Holder(binding) { adapterPosition -> onRuleClick(getItem(adapterPosition).ruleId) }
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    class Holder(
            private val binding: ItemRuleSearchResultBinding,
            private val onClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val context get() = binding.root.context

        init {
            binding.root.setOnClickListener {
                withExistingAdapterPosition { position -> onClick(position) }
            }
        }

        fun bind(item: SearchResultItem) = with(binding) {
            numberTextView.text = context.getString(R.string.rule_rule_number, item.ruleId)
            annotationTextView.text = item.annotation
            snippetTextView.text = item.snippet
        }
    }
}