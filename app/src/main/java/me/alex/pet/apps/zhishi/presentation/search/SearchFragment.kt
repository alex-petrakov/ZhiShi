package me.alex.pet.apps.zhishi.presentation.search

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.databinding.FragmentSearchBinding
import me.alex.pet.apps.zhishi.presentation.common.MaterialZAxisTransition
import me.alex.pet.apps.zhishi.presentation.common.extensions.extendBottomPaddingWithSystemInsets
import me.alex.pet.apps.zhishi.presentation.common.extensions.focusAndShowKeyboard
import me.alex.pet.apps.zhishi.presentation.common.extensions.hideKeyboard

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val viewModel by viewModels<SearchViewModel>()

    private var _binding: FragmentSearchBinding? = null

    private val binding get() = _binding!!

    private var lastRenderedState: ViewState? = null

    private val searchResultsAdapter by lazy {
        SearchResultsAdapter { ruleId ->
            viewModel.onClickRule(ruleId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MaterialZAxisTransition.setupOriginAndDestination(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareView()
        subscribeToModel()
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun prepareView() = with(binding) {
        recyclerView.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                extendBottomPaddingWithSystemInsets()
            }
            setHasFixedSize(true)
            clipToPadding = false
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchResultsAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        viewModel.onScrollResults()
                    }
                }
            })
        }
        toolbar.setNavigationOnClickListener {
            viewModel.onBackPressed()
        }
        queryEt.doAfterTextChanged { editable ->
            if (editable != null) {
                viewModel.onUpdateQuery(editable.toString())
            }
        }
    }

    private fun subscribeToModel() = with(viewModel) {
        viewState.observe(viewLifecycleOwner) { newState -> render(newState) }
        viewEffect.observe(viewLifecycleOwner) { effect -> handle(effect) }
    }

    private fun render(state: ViewState) = with(binding) {
        if (lastRenderedState != null) {
            val autoTransition = AutoTransition().apply {
                duration = 150
                removeTarget(R.id.suggestions_chip_group)
            }
            TransitionManager.beginDelayedTransition(contentFrame, autoTransition)
        }

        recyclerView.isVisible = state.searchResults.isVisible
        searchResultsAdapter.submitList(state.searchResults.items)

        emptyView.isVisible = state.emptyView.isVisible

        suggestionsView.isVisible = state.suggestionsView.isVisible

        if (state.suggestionsView != lastRenderedState?.suggestionsView) {
            val chips = inflateSuggestionChips(state.suggestionsView.suggestions)
            suggestionsChipGroup.removeAllViews()
            chips.forEach { suggestionsChipGroup.addView(it) }
        }

        lastRenderedState = state
    }

    private fun inflateSuggestionChips(suggestions: List<String>) = with(binding) {
        suggestions.map { suggestion ->
            val chip = layoutInflater.inflate(
                R.layout.view_suggestion_chip,
                suggestionsChipGroup,
                false
            ) as Chip
            chip.apply {
                text = suggestion
                id = ViewCompat.generateViewId()
                setOnClickListener { view ->
                    val chipText = (view as Chip).text
                    queryEt.setText(chipText)
                    queryEt.setSelection(chipText.length)
                }
            }
        }
    }

    private fun handle(effect: ViewEffect) {
        when (effect) {
            ViewEffect.SHOW_KEYBOARD -> binding.queryEt.focusAndShowKeyboard()
            ViewEffect.HIDE_KEYBOARD -> requireActivity().hideKeyboard()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {

        private const val STATE_FIRST_START = "FIRST_START"

        fun newInstance() = SearchFragment()
    }
}