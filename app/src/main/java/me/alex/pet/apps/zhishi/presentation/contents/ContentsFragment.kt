package me.alex.pet.apps.zhishi.presentation.contents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.databinding.FragmentContentsBinding
import me.alex.pet.apps.zhishi.presentation.HostActivity
import me.alex.pet.apps.zhishi.presentation.common.extensions.observe
import org.koin.android.viewmodel.ext.android.viewModel

class ContentsFragment : Fragment() {

    private var _binding: FragmentContentsBinding? = null

    private val binding get() = _binding!!

    private val viewModel by viewModel<ContentsViewModel>()

    private val contentsAdapter = ContentsAdapter { clickedSectionId ->
        viewModel.onClickSection(clickedSectionId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentContentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        prepareView()
        subscribeToModel()
    }

    private fun prepareView() = with(binding) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = contentsAdapter
        }
        toolbar.apply {
            inflateMenu(R.menu.main)
            setOnMenuItemClickListener(::handleMenuClick)
        }
    }

    private fun subscribeToModel() = with(viewModel) {
        viewState.observe(viewLifecycleOwner) { newState -> renderState(newState) }
        viewEffect.observe(viewLifecycleOwner) { effect -> handleEffect(effect) }
    }

    private fun handleEffect(effect: ViewEffect) {
        val navigator = requireActivity() as HostActivity
        when (effect) {
            is ViewEffect.NavigateToSection -> navigator.navigateToSection(effect.sectionId)
            is ViewEffect.NavigateToSearch -> navigator.navigateToSearch()
        }
    }

    private fun renderState(state: ViewState) = with(binding) {
        contentsAdapter.items = state.contents
    }

    private fun handleMenuClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                viewModel.onClickSearchAction()
                true
            }
            else -> false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = ContentsFragment()
    }
}