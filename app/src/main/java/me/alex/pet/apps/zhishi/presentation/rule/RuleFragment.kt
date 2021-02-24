package me.alex.pet.apps.zhishi.presentation.rule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.databinding.FragmentRuleBinding
import me.alex.pet.apps.zhishi.presentation.common.observe
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class RuleFragment : Fragment() {

    private var _binding: FragmentRuleBinding? = null

    private val binding get() = _binding!!

    private val viewModel by viewModel<RuleViewModel> {
        val args = requireArguments()
        check(args.containsKey(ARG_RULE_ID)) { "Required rule ID argument is missing" }
        val ruleId = args.getLong(ARG_RULE_ID)
        parametersOf(ruleId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentRuleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        prepareView()
        subscribeToModel()
    }

    private fun prepareView() = with(binding) {
        toolbar.apply {
            setNavigationIcon(R.drawable.ic_action_up)
            setNavigationOnClickListener { requireActivity().onBackPressed() }
        }
    }

    private fun subscribeToModel() = with(viewModel) {
        viewState.observe(viewLifecycleOwner) { newState -> render(newState) }
    }

    private fun render(state: ViewState) = with(binding) {
        toolbar.title = getString(R.string.rule_rule_number, state.ruleNumber)
        ruleContentTv.text = state.ruleContent.string
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {

        private const val ARG_RULE_ID = "RULE_ID"

        fun newInstance(ruleId: Long): RuleFragment {
            val args = Bundle().apply {
                putLong(ARG_RULE_ID, ruleId)
            }
            return RuleFragment().apply {
                arguments = args
            }
        }
    }
}