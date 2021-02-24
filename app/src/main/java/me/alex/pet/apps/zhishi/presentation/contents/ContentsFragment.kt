package me.alex.pet.apps.zhishi.presentation.contents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import me.alex.pet.apps.zhishi.databinding.FragmentContentsBinding
import me.alex.pet.apps.zhishi.presentation.HostActivity
import me.alex.pet.apps.zhishi.presentation.common.observe
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
    }

    private fun subscribeToModel() = with(viewModel) {
        viewState.observe(viewLifecycleOwner) { newState -> renderState(newState) }
        viewEffect.observe(viewLifecycleOwner) { effect -> handleEffect(effect) }
    }

    private fun handleEffect(effect: ViewEffect) {
        when (effect) {
            is ViewEffect.NavigateToSection -> (requireActivity() as HostActivity).navigateToSection(effect.sectionId)
        }
    }

    private fun renderState(state: ViewState) = with(binding) {
        contentsAdapter.items = state.contents
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = ContentsFragment()
    }
}