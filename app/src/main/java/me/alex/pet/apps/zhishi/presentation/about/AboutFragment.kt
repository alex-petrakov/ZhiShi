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
import java.util.*
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

        rateAppButton.setOnClickListener { openAppPageInGooglePlay() }
        emailDeveloperButton.setOnClickListener { composeFeedbackEmail() }
    }

    private fun openWebLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Snackbar.make(binding.root, R.string.about_error_no_web_browser, Snackbar.LENGTH_SHORT)
                .show()
        }
    }

    private fun openAppPageInGooglePlay() {
        val appId = BuildConfig.APPLICATION_ID.removeSuffix(".debug")
        val intent = Intent(Intent.ACTION_VIEW)
            .setData(Uri.parse("https://play.google.com/store/apps/details?id=$appId"))
            .setPackage("com.android.vending")
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Snackbar.make(binding.root, R.string.about_error_no_google_play, Snackbar.LENGTH_SHORT)
                .show()
        }
    }

    private fun composeFeedbackEmail() {
        val appVersion = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        val device = "${Build.MANUFACTURER} ${Build.MODEL}"
        val osVersion = "${Build.VERSION.RELEASE} (${Build.VERSION.SDK_INT})"
        val locale = Locale.getDefault()

        val recipients = arrayOf("alex.petrakov.dev@gmail.com")
        val emailSubject = getString(R.string.feedback_email_subject_template, appVersion)
        val emailBody = getString(
            R.string.feedback_email_body_template,
            appVersion,
            device,
            osVersion,
            locale
        )
        composeEmail(recipients, emailSubject, emailBody)
    }

    private fun composeEmail(recipients: Array<String>, subject: String, body: String) {
        val intent = Intent(Intent.ACTION_SENDTO)
            .setData(Uri.parse("mailto:"))
            .putExtra(Intent.EXTRA_EMAIL, recipients)
            .putExtra(Intent.EXTRA_SUBJECT, subject)
            .putExtra(Intent.EXTRA_TEXT, body)
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Snackbar.make(binding.root, R.string.about_error_no_email_client, Snackbar.LENGTH_SHORT)
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