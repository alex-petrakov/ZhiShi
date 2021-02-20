package me.alex.pet.apps.zhishi.presentation.section

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.databinding.FragmentSectionBinding
import me.alex.pet.apps.zhishi.presentation.common.observe
import me.alex.pet.apps.zhishi.presentation.section.model.SectionViewState
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SectionFragment : Fragment() {

    private var _binding: FragmentSectionBinding? = null

    private val binding get() = _binding!!

    private val viewModel by viewModel<SectionViewModel> {
        val args = requireArguments()
        if (!args.containsKey(ARG_SECTION_ID)) {
            throw IllegalStateException("Required section id argument is missing")
        }
        val sectionId = args.getLong(ARG_SECTION_ID)
        parametersOf(sectionId)
    }

    private val rulesAdapter = RulesAdapter { clickedRuleId ->
        Toast.makeText(requireContext(), "$clickedRuleId", Toast.LENGTH_SHORT).show()
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
    }

    private fun render(state: SectionViewState) = with(binding) {
        toolbar.title = getString(R.string.section_rules_range, state.rulesRange.first, state.rulesRange.last)
        rulesAdapter.items = state.elements
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