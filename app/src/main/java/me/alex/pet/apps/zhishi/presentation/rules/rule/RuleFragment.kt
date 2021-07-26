package me.alex.pet.apps.zhishi.presentation.rules.rule

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import me.alex.pet.apps.zhishi.databinding.FragmentRuleBinding
import me.alex.pet.apps.zhishi.presentation.common.styledtext.StyledTextRenderer
import me.alex.pet.apps.zhishi.presentation.common.styledtext.spanrenderers.BasicCharSpanRenderer
import me.alex.pet.apps.zhishi.presentation.common.styledtext.spanrenderers.CharSpanRenderer
import me.alex.pet.apps.zhishi.presentation.common.styledtext.spanrenderers.LinkRenderer
import me.alex.pet.apps.zhishi.presentation.common.styledtext.spanrenderers.ParagraphSpanRenderer
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class RuleFragment : Fragment() {

    private val viewModel by viewModel<RuleViewModel> {
        val args = requireArguments()
        check(args.containsKey(ARG_RULE_ID)) { "Required rule ID argument is missing" }
        check(args.containsKey(ARG_DISPLAY_SECTION_BUTTON)) { "Required argument is missing" }
        val ruleId = args.getLong(ARG_RULE_ID)
        val displaySectionButton = args.getBoolean(ARG_DISPLAY_SECTION_BUTTON)
        parametersOf(ruleId, displaySectionButton)
    }

    private var _binding: FragmentRuleBinding? = null

    private val binding get() = _binding!!

    private val ruleTextRenderer by lazy {
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

    private val sectionNameRenderer = StyledTextRenderer(
            characterSpansRenderer = BasicCharSpanRenderer()
    )

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
        sectionButton.setOnClickListener { viewModel.onNavigateToSection() }
    }

    private fun subscribeToModel(): Unit = with(viewModel) {
        viewState.observe(viewLifecycleOwner) { newState -> render(newState) }
    }

    private fun render(state: ViewState): Unit = with(binding) {
        ruleTextRenderer.render(state.ruleContent, ruleContentTv)
        sectionButton.isVisible = state.sectionButtonIsVisible
        sectionButton.text = sectionNameRenderer.convertToSpanned(state.sectionName)
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
        private const val ARG_DISPLAY_SECTION_BUTTON = "DISPLAY_SECTION_BUTTON"

        fun newInstance(ruleId: Long, displaySectionButton: Boolean = false): RuleFragment {
            return RuleFragment().apply {
                arguments = bundleOf(
                        ARG_RULE_ID to ruleId,
                        ARG_DISPLAY_SECTION_BUTTON to displaySectionButton
                )
            }
        }
    }
}