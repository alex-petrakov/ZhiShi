package me.alex.pet.apps.zhishi.presentation.about

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.terrakok.cicerone.Router
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import me.alex.pet.apps.zhishi.BuildConfig
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.databinding.FragmentAboutBinding
import me.alex.pet.apps.zhishi.presentation.AppScreens
import me.alex.pet.apps.zhishi.presentation.common.extensions.extendBottomPaddingWithSystemInsets
import javax.inject.Inject

@AndroidEntryPoint
class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null

    private val binding get() = _binding!!

    @Inject
    lateinit var router: Router

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareView()
    }

    private fun prepareView(): Unit = with(binding) {
        scrollView.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                extendBottomPaddingWithSystemInsets()
            }
            clipToPadding = false
        }
        toolbar.setNavigationOnClickListener { router.exit() }
        versionTextView.text = BuildConfig.VERSION_NAME
        inspirationCell.setOnClickListener { openWebLink("https://therules.ru") }
        seeOpenSourceLicensesButton.setOnClickListener { router.navigateTo(AppScreens.licenses()) }
        seePrivacyPolicyButton.setOnClickListener {
            openWebLink("https://alex-petrakov.github.io/ZhiShiPrivacy")
        }
    }

    private fun openWebLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            requireActivity().startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Snackbar.make(binding.root, R.string.about_error_no_web_browser, Snackbar.LENGTH_SHORT)
                .show()
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