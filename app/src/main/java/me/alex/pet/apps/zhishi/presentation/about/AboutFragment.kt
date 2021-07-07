package me.alex.pet.apps.zhishi.presentation.about

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.terrakok.cicerone.Router
import com.google.android.material.snackbar.Snackbar
import me.alex.pet.apps.zhishi.BuildConfig
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.databinding.FragmentAboutBinding
import me.alex.pet.apps.zhishi.presentation.AppScreens
import org.koin.android.ext.android.inject

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null

    private val binding get() = _binding!!

    private val router by inject<Router>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        prepareView()
    }

    private fun prepareView(): Unit = with(binding) {
        toolbar.setNavigationOnClickListener { router.exit() }
        versionTextView.text = BuildConfig.VERSION_NAME
        inspirationCell.setOnClickListener { openTheRulesLink() }
        seeOpenSourceLicensesButton.setOnClickListener { router.navigateTo(AppScreens.licenses()) }
    }

    private fun openTheRulesLink() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://therules.ru"))
        try {
            requireActivity().startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Snackbar.make(binding.root, R.string.about_error_no_web_browser, Snackbar.LENGTH_SHORT)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = AboutFragment()
    }
}