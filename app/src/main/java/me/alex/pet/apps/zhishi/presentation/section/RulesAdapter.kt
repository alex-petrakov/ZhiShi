package me.alex.pet.apps.zhishi.presentation.section

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.databinding.ItemGenericIndentedSubtitleBinding
import me.alex.pet.apps.zhishi.databinding.ItemRuleNumberedBinding
import me.alex.pet.apps.zhishi.presentation.common.extensions.withExistingAdapterPosition
import me.alex.pet.apps.zhishi.presentation.common.styledtext.StyledTextRenderer
import me.alex.pet.apps.zhishi.presentation.common.styledtext.spanrenderers.BasicCharSpanRenderer
import me.alex.pet.apps.zhishi.presentation.common.styledtext.spanrenderers.CharSpanRenderer
import me.alex.pet.apps.zhishi.presentation.common.styledtext.spanrenderers.ParagraphSpanRenderer

class RulesAdapter(
        private val onRuleClick: (Long) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: List<DisplayableItem> = emptyList()
        // Adapter data is updated only on first screen load
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is DisplayableItem.Heading -> R.layout.item_generic_indented_subtitle
            is DisplayableItem.Rule -> R.layout.item_rule_numbered
        }
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.item_generic_indented_subtitle -> {
                HeadingHolder(ItemGenericIndentedSubtitleBinding.bind(view))
            }
            R.layout.item_rule_numbered -> {
                RuleHolder(ItemRuleNumberedBinding.bind(view)) { adapterPosition ->
                    onRuleClick((items[adapterPosition] as DisplayableItem.Rule).id)
                }
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is HeadingHolder -> holder.bind(item as DisplayableItem.Heading)
            is RuleHolder -> holder.bind(item as DisplayableItem.Rule)
            else -> throw IllegalArgumentException("Unknown holder type: ${holder::class.java}")
        }
    }

    private class HeadingHolder(
            private val binding: ItemGenericIndentedSubtitleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val styledTextRenderer = StyledTextRenderer(
                characterSpansRenderer = BasicCharSpanRenderer()
        )

        fun bind(heading: DisplayableItem.Heading): Unit = with(binding) {
            styledTextRenderer.render(heading.content, contentTv)
        }
    }

    private class RuleHolder(
            private val binding: ItemRuleNumberedBinding,
            private val onClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val styledTextRenderer = StyledTextRenderer(
                paragraphSpansRenderer = ParagraphSpanRenderer(theme, binding.contentTv.paint),
                characterSpansRenderer = CharSpanRenderer(theme)
        )

        private val context get() = itemView.context

        private val theme get() = context.theme

        init {
            binding.root.setOnClickListener {
                withExistingAdapterPosition { position -> onClick(position) }
            }
        }

        fun bind(rule: DisplayableItem.Rule): Unit = with(binding) {
            numberTv.text = itemView.context.getString(R.string.app_rule_number, rule.id)
            styledTextRenderer.render(rule.content, contentTv)
        }
    }
}
