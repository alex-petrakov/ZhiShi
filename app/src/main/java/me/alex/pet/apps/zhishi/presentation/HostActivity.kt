package me.alex.pet.apps.zhishi.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
        val binding = ActivityHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

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