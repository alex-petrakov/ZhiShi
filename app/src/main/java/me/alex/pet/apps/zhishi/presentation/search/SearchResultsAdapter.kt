package me.alex.pet.apps.zhishi.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.databinding.ItemRuleSearchResultBinding
import me.alex.pet.apps.zhishi.presentation.common.extensions.withExistingAdapterPosition

class SearchResultsAdapter(
    private val onRuleClick: (ruleId: Long) -> Unit,
    private val onListChanged: ListChangeCallback
) : ListAdapter<SearchResultUiModel, SearchResultsAdapter.Holder>(SearchResultUiModel.DiffCallback) {

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

    override fun onCurrentListChanged(
        previousList: MutableList<SearchResultUiModel>,
        currentList: MutableList<SearchResultUiModel>
    ) {
        onListChanged(previousList, currentList)
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

        fun bind(searchResult: SearchResultUiModel) = with(binding) {
            numberTextView.text = context.getString(R.string.rule_rule_number, searchResult.ruleId)
            annotationTextView.text = searchResult.annotation
            snippetTextView.text = searchResult.snippet
        }
    }
}

typealias ListChangeCallback =
            (prevList: List<SearchResultUiModel>, currentList: List<SearchResultUiModel>) -> Unit