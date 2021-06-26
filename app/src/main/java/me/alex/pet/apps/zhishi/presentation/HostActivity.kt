package me.alex.pet.apps.zhishi.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Replace
import com.github.terrakok.cicerone.androidx.AppNavigator
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.databinding.ActivityHostBinding
import org.koin.android.ext.android.inject

class HostActivity : AppCompatActivity() {

    private val navigator = AppNavigator(this, R.id.fragment_container)

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