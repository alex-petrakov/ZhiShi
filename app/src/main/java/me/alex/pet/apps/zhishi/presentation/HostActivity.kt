package me.alex.pet.apps.zhishi.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.databinding.ActivityHostBinding
import me.alex.pet.apps.zhishi.presentation.home.HomeFragment

class HostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, HomeFragment.newInstance())
                    .commit()
        }
    }
}