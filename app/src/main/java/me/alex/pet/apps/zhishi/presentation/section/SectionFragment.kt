package me.alex.pet.apps.zhishi.presentation.section

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.databinding.FragmentSectionBinding
import me.alex.pet.apps.zhishi.presentation.HostActivity
import me.alex.pet.apps.zhishi.presentation.common.observe
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SectionFragment : Fragment() {

    private var _binding: FragmentSectionBinding? = null

    private val binding get() = _binding!!

    private val viewModel by viewModel<SectionViewModel> {
        val args = requireArguments()
        check(args.containsKey(ARG_SECTION_ID)) { "Required section id argument is missing" }
        val sectionId = args.getLong(ARG_SECTION_ID)
        parametersOf(sectionId)
    }

    private val rulesAdapter by lazy {
        RulesAdapter(requireActivity().theme) { clickedRuleId ->
            viewModel.onClickRule(clickedRuleId)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        prepareView()
        subscribeToModel()
    }

    private fun prepareView() = with(binding) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = rulesAdapter
        }
        toolbar.apply {
            setNavigationIcon(R.drawable.ic_action_up)
            setNavigationContentDescription(R.string.app_navigate_up)
        }
    }

    private fun subscribeToModel() = with(viewModel) {
        viewState.observe(viewLifecycleOwner) { newState -> render(newState) }
        viewEffect.observe(viewLifecycleOwner) { effect -> handle(effect) }
    }

    private fun render(state: ViewState) = with(binding) {
        val ruleNumbersRange = state.ruleNumbersRange
        toolbar.title = when (ruleNumbersRange.first) {
            ruleNumbersRange.last -> getString(R.string.section_rule_number, ruleNumbersRange.first)
            else -> getString(R.string.section_rule_numbers_range, ruleNumbersRange.first, ruleNumbersRange.last)
        }
        rulesAdapter.items = state.elements
    }

    private fun handle(effect: ViewEffect) {
        when (effect) {
            is ViewEffect.NavigateToRule -> (requireActivity() as HostActivity).navigateToRule(effect.rulesToDisplay)
        }
    }

    override fun onStart() {
        super.onStart()
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {

        private const val ARG_SECTION_ID = "SECTION_ID"

        fun newInstance(sectionId: Long): SectionFragment {
            val args = Bundle().apply {
                putLong(ARG_SECTION_ID, sectionId)
            }
            return SectionFragment().apply {
                arguments = args
            }
        }
    }
}