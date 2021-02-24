package me.alex.pet.apps.zhishi.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import me.alex.pet.apps.zhishi.databinding.FragmentHomeBinding
import me.alex.pet.apps.zhishi.presentation.HostActivity
import me.alex.pet.apps.zhishi.presentation.common.observe
import me.alex.pet.apps.zhishi.presentation.home.model.HomeViewEffect
import me.alex.pet.apps.zhishi.presentation.home.model.HomeViewState
import org.koin.android.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    private val viewModel by viewModel<HomeViewModel>()

    private val contentsAdapter = ContentsAdapter { clickedSectionId ->
        viewModel.onClickSection(clickedSectionId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
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

    private fun handleEffect(effect: HomeViewEffect) {
        when (effect) {
            is HomeViewEffect.NavigateToSection -> (requireActivity() as HostActivity).navigateToSection(effect.sectionId)
        }
    }

    private fun renderState(state: HomeViewState) = with(binding) {
        contentsAdapter.items = state.contents
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}