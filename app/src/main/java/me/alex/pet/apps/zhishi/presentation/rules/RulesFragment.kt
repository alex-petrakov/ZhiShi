package me.alex.pet.apps.zhishi.presentation.rules

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.databinding.FragmentRulesBinding
import me.alex.pet.apps.zhishi.presentation.rules.RulesViewModel.Companion.ARG_RULES_TO_DISPLAY
import me.alex.pet.apps.zhishi.presentation.rules.rule.RuleFragment

@AndroidEntryPoint
class RulesFragment : Fragment() {

    private val viewModel by viewModels<RulesViewModel>()

    private var _binding: FragmentRulesBinding? = null

    private val binding get() = _binding!!

    private val rulesToDisplay: RulesToDisplay
        get() = requireArguments().getParcelable(ARG_RULES_TO_DISPLAY)
            ?: throw IllegalStateException("Required argument is missing")

    private val displaySectionButton: Boolean
        get() = requireArguments().getBoolean(ARG_DISPLAY_SECTION_BUTTON)

    private val rulesAdapter by lazy {
        RulesAdapter(this@RulesFragment, rulesToDisplay.ids, displaySectionButton)
    }

    private lateinit var nextPageMenuItem: MenuItem

    private lateinit var prevPageMenuItem: MenuItem

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            viewModel.onRuleSelected(position)
            nextPageMenuItem.isEnabled = position < rulesToDisplay.ids.lastIndex
            prevPageMenuItem.isEnabled = position > 0
        }
    }

    private val menuItemClickListener = Toolbar.OnMenuItemClickListener { item ->

        fun dispatchMenuItemClickToChildFragment(item: MenuItem): Boolean {
            val selectedPosition = binding.viewPager.currentItem
            val fragment = rulesAdapter.findFragmentAt(selectedPosition) as? RuleFragment
            return fragment?.onMenuItemClick(item.itemId) ?: false
        }

        fun handleMenuItemClick(item: MenuItem) = when (item.itemId) {
            R.id.action_go_to_next_page -> {
                binding.viewPager.currentItem += 1
                true
            }
            R.id.action_go_to_prev_page -> {
                binding.viewPager.currentItem -= 1
                true
            }
            else -> false
        }

        val eventWasConsumedByChildFragment = dispatchMenuItemClickToChildFragment(item)
        return@OnMenuItemClickListener if (eventWasConsumedByChildFragment) {
            true
        } else {
            handleMenuItemClick(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRulesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareView(savedInstanceState)
        subscribeToModel()
    }

    private fun prepareView(savedInstanceState: Bundle?): Unit = with(binding) {
        prepareToolbar()

        viewPager.apply {
            adapter = rulesAdapter
            registerOnPageChangeCallback(onPageChangeCallback)
            if (savedInstanceState == null) {
                setCurrentItem(rulesToDisplay.selectionIndex, false)
            }
        }
    }

    private fun prepareToolbar() = with(binding.toolbar) {
        inflateMenu(R.menu.rules)
        nextPageMenuItem = menu.findItem(R.id.action_go_to_next_page)
        prevPageMenuItem = menu.findItem(R.id.action_go_to_prev_page)

        setNavigationOnClickListener { viewModel.onBackPressed() }
        setOnMenuItemClickListener(menuItemClickListener)
    }

    private fun subscribeToModel(): Unit = with(viewModel) {
        viewState.observe(viewLifecycleOwner) { state -> render(state) }
    }

    private fun render(state: ViewState): Unit = with(binding) {
        toolbar.title = getString(R.string.rule_rule_number, state.selectedRuleId)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {

        private const val ARG_DISPLAY_SECTION_BUTTON = "DISPLAY_SECTION_BUTTON"

        fun newInstance(
            rulesToDisplay: RulesToDisplay,
            displaySectionButton: Boolean = false
        ): RulesFragment {
            return RulesFragment().apply {
                arguments = bundleOf(
                    ARG_RULES_TO_DISPLAY to rulesToDisplay,
                    ARG_DISPLAY_SECTION_BUTTON to displaySectionButton
                )
            }
        }
    }
}