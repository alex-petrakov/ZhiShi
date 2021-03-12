package me.alex.pet.apps.zhishi.presentation.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.databinding.FragmentSearchBinding
import me.alex.pet.apps.zhishi.presentation.HostActivity
import me.alex.pet.apps.zhishi.presentation.common.textChanges
import org.koin.android.viewmodel.ext.android.viewModel

@FlowPreview
@ExperimentalCoroutinesApi
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null

    private val binding get() = _binding!!

    private val viewModel by viewModel<SearchViewModel>()

    private var lastRenderedState: ViewState? = null

    private val searchResultsAdapter by lazy {
        SearchResultsAdapter(requireActivity().theme) { ruleId ->
            viewModel.onClickRule(ruleId)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
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
            adapter = searchResultsAdapter
        }
        toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
        queryEt.textChanges()
                .debounce(300)
                .onEach { viewModel.onUpdateQuery(it.toString()) }
                .launchIn(viewLifecycleOwner.lifecycle.coroutineScope)
    }

    private fun subscribeToModel() = with(viewModel) {
        viewState.observe(viewLifecycleOwner) { newState -> render(newState) }
        viewEffect.observe(viewLifecycleOwner) { effect -> handle(effect) }
    }

    private fun render(state: ViewState) = with(binding) {
        TransitionManager.beginDelayedTransition(contentFrame)

        recyclerView.isVisible = state.searchResults.isVisible
        searchResultsAdapter.items = state.searchResults.items

        emptyView.isVisible = state.emptyView.isVisible

        suggestionsView.isVisible = state.suggestionsView.isVisible

        if (state.suggestionsView != lastRenderedState?.suggestionsView) {
            suggestionsChipGroup.removeAllViews()
            state.suggestionsView.suggestions.forEach { suggestion ->
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
                suggestionsChipGroup.addView(chip)
            }
        }
        lastRenderedState = state
    }

    private fun handle(effect: ViewEffect) {
        when (effect) {
            is ViewEffect.NavigateToRule -> (requireActivity() as HostActivity).navigateToRule(effect.ruleId)
        }
    }

    companion object {
        fun newInstance() = SearchFragment()
    }
}

private val ChipGroup.chips get() = this.children.mapNotNull { it as? Chip }