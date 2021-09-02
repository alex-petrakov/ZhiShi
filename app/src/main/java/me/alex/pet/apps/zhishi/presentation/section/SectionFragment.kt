package me.alex.pet.apps.zhishi.presentation.section

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.databinding.FragmentSectionBinding
import me.alex.pet.apps.zhishi.presentation.common.extensions.extendBottomPaddingWithSystemInsets
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SectionFragment : Fragment() {

    private val viewModel by viewModel<SectionViewModel> {
        val args = requireArguments()
        check(args.containsKey(ARG_SECTION_ID)) { "Required section ID argument is missing" }
        val sectionId = args.getLong(ARG_SECTION_ID)
        parametersOf(sectionId)
    }

    private var _binding: FragmentSectionBinding? = null

    private val binding get() = _binding!!

    private val rulesAdapter = RulesAdapter { clickedRuleId ->
        viewModel.onClickRule(clickedRuleId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareView()
        subscribeToModel()
    }

    private fun prepareView(): Unit = with(binding) {
        recyclerView.apply {
            extendBottomPaddingWithSystemInsets()
            clipToPadding = false
            layoutManager = LinearLayoutManager(requireContext())
            adapter = rulesAdapter
        }
        toolbar.apply {
            setNavigationIcon(R.drawable.ic_action_up)
            setNavigationContentDescription(R.string.app_navigate_up)
            setNavigationOnClickListener { viewModel.onBackPressed() }
        }
    }

    private fun subscribeToModel(): Unit = with(viewModel) {
        viewState.observe(viewLifecycleOwner) { newState -> render(newState) }
    }

    private fun render(state: ViewState): Unit = with(binding) {
        val ruleNumbersRange = state.ruleNumbersRange
        toolbar.title = when (ruleNumbersRange.first) {
            ruleNumbersRange.last -> getString(R.string.section_rule_number, ruleNumbersRange.first)
            else -> getString(R.string.section_rule_numbers_range, ruleNumbersRange.first, ruleNumbersRange.last)
        }
        rulesAdapter.items = state.listItems
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {

        private const val ARG_SECTION_ID = "SECTION_ID"

        fun newInstance(sectionId: Long): SectionFragment {
            return SectionFragment().apply {
                arguments = bundleOf(ARG_SECTION_ID to sectionId)
            }
        }
    }
}