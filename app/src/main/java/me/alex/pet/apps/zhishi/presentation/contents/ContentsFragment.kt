package me.alex.pet.apps.zhishi.presentation.contents

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.databinding.FragmentContentsBinding
import me.alex.pet.apps.zhishi.presentation.common.extensions.extendBottomPaddingWithSystemInsets

@AndroidEntryPoint
class ContentsFragment : Fragment() {

    private var _binding: FragmentContentsBinding? = null

    private val binding get() = _binding!!

    private val viewModel by viewModels<ContentsViewModel>()

    private val contentsAdapter = ContentsAdapter { clickedSectionId ->
        viewModel.onClickSection(clickedSectionId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentContentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareView()
        subscribeToModel()
    }

    private fun prepareView(): Unit = with(binding) {
        recyclerView.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                extendBottomPaddingWithSystemInsets()
            }
            setHasFixedSize(true)
            clipToPadding = false
            layoutManager = LinearLayoutManager(context)
            adapter = contentsAdapter
        }
        toolbar.apply {
            inflateMenu(R.menu.main)
            setOnMenuItemClickListener(::handleMenuClick)
        }
    }

    private fun subscribeToModel(): Unit = with(viewModel) {
        viewState.observe(viewLifecycleOwner) { newState -> render(newState) }
    }

    private fun render(state: ViewState) {
        contentsAdapter.items = state.listItems
    }

    private fun handleMenuClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                viewModel.onClickSearchAction()
                true
            }
            R.id.action_about -> {
                viewModel.onClickAboutAction()
                true
            }
            else -> false
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = ContentsFragment()
    }
}