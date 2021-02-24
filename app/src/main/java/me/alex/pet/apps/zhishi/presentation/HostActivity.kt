package me.alex.pet.apps.zhishi.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.databinding.ActivityHostBinding
import me.alex.pet.apps.zhishi.presentation.home.HomeFragment
import me.alex.pet.apps.zhishi.presentation.rule.RuleFragment
import me.alex.pet.apps.zhishi.presentation.section.SectionFragment

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

    fun navigateToSection(sectionId: Long) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SectionFragment.newInstance(sectionId))
                .addToBackStack("SECTION")
                .commit()
    }

    fun navigateToRule(ruleId: Long) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RuleFragment.newInstance(ruleId))
                .addToBackStack("RULE")
                .commit()
    }

    fun navigateBack() {
        supportFragmentManager.popBackStack()
    }
}