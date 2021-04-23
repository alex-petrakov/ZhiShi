package me.alex.pet.apps.zhishi.presentation.rules

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import me.alex.pet.apps.zhishi.presentation.rules.rule.RuleFragment

class RulesAdapter(val fragment: Fragment) : FragmentStateAdapter(fragment) {

    var ruleIds: List<Long> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = ruleIds.size

    override fun createFragment(position: Int) = RuleFragment.newInstance(ruleIds[position])

    override fun onViewDetachedFromWindow(holder: FragmentViewHolder) {
        super.onViewDetachedFromWindow(holder)
        val fragment = fragment.childFragmentManager.findFragmentByTag("f${holder.adapterPosition}") as RuleFragment
        fragment.resetScroll()
    }
}