package me.alex.pet.apps.zhishi.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.databinding.ActivityHostBinding
import me.alex.pet.apps.zhishi.presentation.contents.ContentsFragment
import me.alex.pet.apps.zhishi.presentation.rules.RulesFragment
import me.alex.pet.apps.zhishi.presentation.rules.RulesToDisplay
import me.alex.pet.apps.zhishi.presentation.search.SearchFragment
import me.alex.pet.apps.zhishi.presentation.section.SectionFragment

class HostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, ContentsFragment.newInstance())
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
                .replace(R.id.fragment_container, RulesFragment.newInstance(ruleId))
                .addToBackStack("RULE")
                .commit()
    }

    fun navigateToRule(rulesToDisplay: RulesToDisplay) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RulesFragment.newInstance(rulesToDisplay))
                .addToBackStack("RULE")
                .commit()
    }

    fun navigateToSearch() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SearchFragment.newInstance())
                .addToBackStack("SEARCH")
                .commit()
    }

    fun navigateBack() {
        supportFragmentManager.popBackStack()
    }
}