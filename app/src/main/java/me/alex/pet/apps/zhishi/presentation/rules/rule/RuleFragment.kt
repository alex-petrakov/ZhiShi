package me.alex.pet.apps.zhishi.presentation.rules.rule

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import me.alex.pet.apps.zhishi.databinding.FragmentRuleBinding
import me.alex.pet.apps.zhishi.presentation.HostActivity
import me.alex.pet.apps.zhishi.presentation.common.styledtext.StyledTextRenderer
import me.alex.pet.apps.zhishi.presentation.common.styledtext.spanrenderers.CharSpanRenderer
import me.alex.pet.apps.zhishi.presentation.common.styledtext.spanrenderers.LinkRenderer
import me.alex.pet.apps.zhishi.presentation.common.styledtext.spanrenderers.ParagraphSpanRenderer
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class RuleFragment : Fragment() {

    private val viewModel by viewModel<RuleViewModel> {
        val args = requireArguments()
        check(args.containsKey(ARG_RULE_ID)) { "Required rule ID argument is missing" }
        val ruleId = args.getLong(ARG_RULE_ID)
        parametersOf(ruleId)
    }

    private var _binding: FragmentRuleBinding? = null

    private val binding get() = _binding!!

    private val styledTextConverter by lazy {
        StyledTextRenderer(
                paragraphSpansRenderer = ParagraphSpanRenderer(
                        requireActivity().theme,
                        binding.ruleContentTv.paint
                ),
                characterSpansRenderer = CharSpanRenderer(requireActivity().theme),
                linksRenderer = LinkRenderer { clickedRuleId ->
                    viewModel.onRuleLinkClick(clickedRuleId)
                }
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRuleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        prepareView()
        subscribeToModel()
    }

    private fun prepareView(): Unit = with(binding) {
        ruleContentTv.movementMethod = LinkMovementMethod() // TODO: Use BetterLinkMovementMethod
    }

    private fun subscribeToModel(): Unit = with(viewModel) {
        viewState.observe(viewLifecycleOwner) { newState -> render(newState) }
        viewEffect.observe(viewLifecycleOwner) { effect -> handle(effect) }
    }

    private fun render(state: ViewState): Unit = with(binding) {
        styledTextConverter.render(state.ruleContent, ruleContentTv)
    }

    private fun handle(effect: ViewEffect) {
        when (effect) {
            is ViewEffect.NavigateToRule -> (requireActivity() as HostActivity).navigateToRule(effect.rulesToDisplay)
        }
    }

    fun resetScroll() {
        _binding?.scrollView?.scrollTo(0, 0)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {

        private const val ARG_RULE_ID = "RULE_ID"

        fun newInstance(ruleId: Long): RuleFragment {
            return RuleFragment().apply {
                arguments = bundleOf(ARG_RULE_ID to ruleId)
            }
        }
    }
}