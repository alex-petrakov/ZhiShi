package me.alex.pet.apps.zhishi.presentation.section

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.presentation.common.toSpannedString

class RulesAdapter(private val onRuleClick: (Long) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: List<DisplayableElement> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is DisplayableElement.Heading -> R.layout.item_generic_indented_subtitle
            is DisplayableElement.Rule -> R.layout.item_rule_numbered
        }
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.item_generic_indented_subtitle -> HeadingHolder(view)
            R.layout.item_rule_numbered -> RuleHolder(view, onRuleClick)
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is HeadingHolder -> holder.bind(item as DisplayableElement.Heading)
            is RuleHolder -> holder.bind(item as DisplayableElement.Rule)
            else -> throw IllegalArgumentException("Unknown holder type: ${holder::class.java}")
        }
    }

    private class HeadingHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val contentTextView = itemView.findViewById<TextView>(R.id.content_tv)

        fun bind(heading: DisplayableElement.Heading) {
            contentTextView.text = heading.content.toSpannedString()
        }
    }

    private class RuleHolder(
            itemView: View,
            private val onRuleClick: (Long) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val numberTextView = itemView.findViewById<TextView>(R.id.number_tv)
        private val contentTextView = itemView.findViewById<TextView>(R.id.content_tv)

        fun bind(rule: DisplayableElement.Rule) {
            itemView.setOnClickListener { onRuleClick(rule.id) }
            numberTextView.text = itemView.context.getString(R.string.app_rule_number, rule.number)
            contentTextView.text = rule.content.toSpannedString()
        }
    }
}
