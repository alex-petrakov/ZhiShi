package me.alex.pet.apps.zhishi.presentation.section

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.databinding.FragmentSectionBinding
import me.alex.pet.apps.zhishi.presentation.common.extensions.extendBottomPaddingWithSystemInsets
import me.alex.pet.apps.zhishi.presentation.section.SectionViewModel.Companion.ARG_SECTION_ID

@AndroidEntryPoint
class SectionFragment : Fragment() {

    private val viewModel by viewModels<SectionViewModel>()

    private var _binding: FragmentSectionBinding? = null

    private val binding get() = _binding!!

    private val rulesAdapter = RulesAdapter { clickedRuleId ->
        viewModel.onClickRule(clickedRuleId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                extendBottomPaddingWithSystemInsets()
            }
            setHasFixedSize(true)
            clipToPadding = false
            layoutManager = LinearLayoutManager(requireContext())
            adapter = rulesAdapter
        }
        toolbar.setNavigationOnClickListener { viewModel.onBackPressed() }
    }

    private fun subscribeToModel(): Unit = with(viewModel) {
        viewState.observe(viewLifecycleOwner) { newState -> render(newState) }
    }

    private fun render(state: ViewState): Unit = with(binding) {
        val ruleNumbersRange = state.ruleNumbersRange
        toolbar.title = when (ruleNumbersRange.first) {
            ruleNumbersRange.last -> getString(R.string.section_rule_number, ruleNumbersRange.first)
            else -> getString(
                R.string.section_rule_numbers_range,
                ruleNumbersRange.first,
                ruleNumbersRange.last
            )
        }
        rulesAdapter.items = state.listItems
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {

        fun newInstance(sectionId: Long): SectionFragment {
            return SectionFragment().apply {
                arguments = bundleOf(ARG_SECTION_ID to sectionId)
            }
        }
    }
}