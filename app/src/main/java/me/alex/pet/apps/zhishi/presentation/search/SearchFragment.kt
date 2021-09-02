package me.alex.pet.apps.zhishi.presentation.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.google.android.material.chip.Chip
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.databinding.FragmentSearchBinding
import me.alex.pet.apps.zhishi.presentation.common.extensions.extendBottomPaddingWithSystemInsets
import me.alex.pet.apps.zhishi.presentation.common.extensions.focusAndShowKeyboard
import me.alex.pet.apps.zhishi.presentation.common.extensions.hideKeyboard
import me.alex.pet.apps.zhishi.presentation.common.extensions.textChanges
import org.koin.androidx.viewmodel.ext.android.viewModel

@FlowPreview
@ExperimentalCoroutinesApi
class SearchFragment : Fragment() {

    private val viewModel by viewModel<SearchViewModel>()

    private var _binding: FragmentSearchBinding? = null

    private val binding get() = _binding!!

    private var lastRenderedState: ViewState? = null

    private val searchResultsAdapter by lazy {
        SearchResultsAdapter { ruleId ->
            viewModel.onClickRule(ruleId)
        }
    }

    private var firstStart = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firstStart = savedInstanceState?.getBoolean(STATE_FIRST_START, true) ?: true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareView()
        subscribeToModel()
    }

    private fun prepareView() = with(binding) {
        recyclerView.apply {
            extendBottomPaddingWithSystemInsets()
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
        queryEt.textChanges()
                .debounce(300)
                .onEach { viewModel.onUpdateQuery(it.toString()) }
                .launchIn(viewLifecycleOwner.lifecycleScope)
        if (firstStart) {
            queryEt.post { queryEt.focusAndShowKeyboard() }
            firstStart = false
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
            ViewEffect.HIDE_KEYBOARD -> requireActivity().hideKeyboard()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(STATE_FIRST_START, firstStart)
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