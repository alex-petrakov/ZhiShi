package me.alex.pet.apps.zhishi.presentation.rules

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.databinding.FragmentRulesBinding
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class RulesFragment : Fragment() {

    private val viewModel by viewModel<RulesViewModel> {
        val rulesToDisplay = requireArguments().getParcelable<RulesToDisplay>(ARG_RULES_TO_DISPLAY)
                ?: throw IllegalStateException("Required argument is missing")
        parametersOf(rulesToDisplay)
    }

    private var _binding: FragmentRulesBinding? = null

    private val binding get() = _binding!!

    private val rulesAdapter by lazy { RulesAdapter(this) }

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            viewModel.onRuleSelected(position)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRulesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        prepareView(savedInstanceState)
        subscribeToModel()
    }

    private fun prepareView(savedInstanceState: Bundle?) = with(binding) {
        toolbar.apply {
            setNavigationIcon(R.drawable.ic_action_up)
            setNavigationOnClickListener { requireActivity().onBackPressed() }
        }

        val rulesToDisplay = requireArguments().getParcelable<RulesToDisplay>(ARG_RULES_TO_DISPLAY)
                ?: throw IllegalStateException("Required argument is missing")
        rulesAdapter.ruleIds = rulesToDisplay.ids
        viewPager.apply {
            adapter = rulesAdapter
            registerOnPageChangeCallback(onPageChangeCallback)
            if (savedInstanceState == null) {
                setCurrentItem(rulesToDisplay.selectionIndex, false)
            }
        }
    }

    private fun subscribeToModel() = with(viewModel) {
        viewState.observe(viewLifecycleOwner) { state -> render(state) }
    }

    private fun render(state: ViewState) = with(binding) {
        toolbar.title = getString(R.string.rule_rule_number, state.selectedRuleNumber)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {

        private const val ARG_RULES_TO_DISPLAY = "RULES_TO_DISPLAY"

        fun newInstance(rulesToDisplay: RulesToDisplay): RulesFragment {
            val argsBundle = Bundle().apply {
                putParcelable(ARG_RULES_TO_DISPLAY, rulesToDisplay)
            }
            return RulesFragment().apply {
                arguments = argsBundle
            }
        }

        fun newInstance(ruleId: Long): RulesFragment {
            return newInstance(RulesToDisplay(ruleId))
        }
    }
}