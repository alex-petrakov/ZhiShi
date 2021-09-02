package me.alex.pet.apps.zhishi.presentation

import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Replace
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.google.android.material.transition.MaterialSharedAxis
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.databinding.ActivityHostBinding
import org.koin.android.ext.android.inject

class HostActivity : AppCompatActivity() {

    private val navigator = object : AppNavigator(this, R.id.fragment_container) {
        override fun setupFragmentTransaction(
                screen: FragmentScreen,
                fragmentTransaction: FragmentTransaction,
                currentFragment: Fragment?,
                nextFragment: Fragment
        ) {
            if (currentFragment == null) {
                return
            }
            currentFragment.apply {
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
            }
            nextFragment.apply {
                enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
                returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
            }
        }
    }

    private val navigatorHolder by inject<NavigatorHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Go edge-to-edge on Android 10+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }

        val binding = ActivityHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Do not let screen content to overlap with the status bar
        // and don't let screen content to be obscured by navigation bars
        // that appear on the side of the screen (in 2 or 3-button navigation modes)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
                val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    topMargin = systemBars.top
                    leftMargin = systemBars.left
                    rightMargin = systemBars.right
                }
                windowInsets
            }
        }

        if (savedInstanceState == null) {
            navigator.applyCommands(arrayOf(Replace(AppScreens.contents())))
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }
}