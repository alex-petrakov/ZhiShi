package me.alex.pet.apps.zhishi.presentation.rule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import me.alex.pet.apps.zhishi.databinding.FragmentRuleBinding
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
        // TODO: Use the ViewModel
        val ruleId = requireArguments().getLong(ARG_RULE_ID)
        binding.textView.text = "Rule ID $ruleId"
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