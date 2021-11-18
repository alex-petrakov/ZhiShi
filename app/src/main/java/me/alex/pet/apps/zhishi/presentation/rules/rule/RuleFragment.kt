package me.alex.pet.apps.zhishi.presentation.rules.rule

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
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
import java.io.ByteArrayOutputStream

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
            R.id.action_share_as_text -> {
                viewModel.onShareRuleText()
                true
            }
            R.id.action_share_as_image -> {
                viewModel.onShareRuleAsImage()
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
            is ViewEffect.ShareRuleSnapshot -> shareImageThroughShareSheet(effect.snapshotUri)
            ViewEffect.RequestViewSnapshot -> captureScreenshotAndShareIt()
        }
    }

    private fun shareTextThroughShareSheet(text: String) {
        ShareCompat.IntentBuilder(requireContext())
            .setType(MIME_TYPE_PLAIN_TEXT)
            .setText(text)
            .startChooser()
    }

    private fun shareImageThroughShareSheet(uri: Uri) {
        ShareCompat.IntentBuilder(requireContext())
            .setType(MIME_TYPE_JPEG_IMAGE)
            .addStream(uri)
            .startChooser()
    }

    private fun captureScreenshotAndShareIt(): Unit = with(binding) {
        // TODO: (1) consider scaling the bitmap
        val bitmap =
            Bitmap.createBitmap(ruleContentTv.width, ruleContentTv.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap).apply {
            drawColor(Color.WHITE) // TODO: (2) use theme attribute
        }

        root.draw(canvas)

        // TODO: (3) consider compressing the bitmap only once
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

        val bytes = outputStream.toByteArray()
        bitmap.recycle()
        viewModel.onScreenshotCaptured(bytes)
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
        private const val MIME_TYPE_JPEG_IMAGE = "image/jpeg"

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