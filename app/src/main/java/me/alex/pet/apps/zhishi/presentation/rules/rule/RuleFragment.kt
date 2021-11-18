package me.alex.pet.apps.zhishi.presentation.rules.rule

import android.os.Build
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.databinding.FragmentRuleBinding
import me.alex.pet.apps.zhishi.presentation.common.extensions.extendBottomPaddingWithSystemInsets
import me.alex.pet.apps.zhishi.presentation.common.styledtext.StyledTextRenderer
import me.alex.pet.apps.zhishi.presentation.common.styledtext.spanrenderers.BasicCharSpanRenderer
import me.alex.pet.apps.zhishi.presentation.common.styledtext.spanrenderers.CharSpanRenderer
import me.alex.pet.apps.zhishi.presentation.common.styledtext.spanrenderers.LinkRenderer
import me.alex.pet.apps.zhishi.presentation.common.styledtext.spanrenderers.ParagraphSpanRenderer
import me.alex.pet.apps.zhishi.presentation.rules.rule.RuleViewModel.Companion.ARG_DISPLAY_SECTION_BUTTON
import me.alex.pet.apps.zhishi.presentation.rules.rule.RuleViewModel.Companion.ARG_RULE_ID

@AndroidEntryPoint
class RuleFragment : Fragment() {

    private val viewModel by viewModels<RuleViewModel>()

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRuleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareView()
        subscribeToModel()
    }

    fun onMenuItemClick(itemId: Int): Boolean {
        return when (itemId) {
            R.id.action_share -> {
                viewModel.onShareRuleText()
                true
            }
            else -> false
        }
    }

    private fun prepareView(): Unit = with(binding) {
        scrollView.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                extendBottomPaddingWithSystemInsets()
            }
            clipToPadding = false
        }
        ruleContentTv.movementMethod = LinkMovementMethod()
        sectionButton.setOnClickListener { viewModel.onNavigateToSection() }
    }

    private fun subscribeToModel(): Unit = with(viewModel) {
        viewState.observe(viewLifecycleOwner) { newState -> render(newState) }
        viewEffect.observe(viewLifecycleOwner) { effect -> handle(effect) }
    }

    private fun render(state: ViewState): Unit = with(binding) {
        ruleTextRenderer.render(state.ruleContent, ruleContentTv)
        sectionButton.isVisible = state.sectionButtonIsVisible
        sectionButton.text = sectionNameRenderer.convertToSpanned(state.sectionName)
    }

    private fun handle(effect: ViewEffect) {
        when (effect) {
            is ViewEffect.ShareRuleText -> shareTextThroughShareSheet(effect.text)
        }
    }

    private fun shareTextThroughShareSheet(text: String) {
        ShareCompat.IntentBuilder(requireActivity())
            .setText(text)
            .setType(MIME_TYPE_PLAIN_TEXT)
            .startChooser()
    }

    fun resetScroll() {
        _binding?.scrollView?.scrollTo(0, 0)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {

        private const val MIME_TYPE_PLAIN_TEXT = "text/plain"

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