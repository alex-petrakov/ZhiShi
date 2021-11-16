package me.alex.pet.apps.zhishi.presentation.rules

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import me.alex.pet.apps.zhishi.presentation.rules.rule.RuleFragment

class RulesAdapter(
        private val fragment: Fragment,
        ruleIds: List<Long> = emptyList(),
        private val displaySectionButton: Boolean
) : FragmentStateAdapter(fragment) {

    var ruleIds: List<Long> = ruleIds
        // Adapter data is updated only on first screen load
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = ruleIds.size

    override fun createFragment(position: Int) = RuleFragment.newInstance(
        ruleIds[position],
        displaySectionButton
    )

    override fun onViewDetachedFromWindow(holder: FragmentViewHolder) {
        super.onViewDetachedFromWindow(holder)
        val fragment = findFragmentAt(holder.adapterPosition) as? RuleFragment
        fragment?.resetScroll()
    }

    fun findFragmentAt(position: Int): Fragment? {
        return fragment.childFragmentManager.findFragmentByTag("f$position")
    }
}