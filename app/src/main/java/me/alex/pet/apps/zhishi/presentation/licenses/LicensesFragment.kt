package me.alex.pet.apps.zhishi.presentation.licenses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.terrakok.cicerone.Router
import me.alex.pet.apps.zhishi.databinding.FragmentLicensesBinding
import org.koin.android.ext.android.inject

class LicensesFragment : Fragment() {

    private var _binding: FragmentLicensesBinding? = null

    private val binding get() = _binding!!

    private val router by inject<Router>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLicensesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareView()
    }

    private fun prepareView(): Unit = with(binding) {
        toolbar.setNavigationOnClickListener { router.exit() }
        webView.loadUrl("file:///android_asset/licenses.html")
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = LicensesFragment()
    }
}