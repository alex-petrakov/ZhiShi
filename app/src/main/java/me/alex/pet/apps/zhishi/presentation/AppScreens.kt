package me.alex.pet.apps.zhishi.presentation

import com.github.terrakok.cicerone.androidx.FragmentScreen
import me.alex.pet.apps.zhishi.presentation.contents.ContentsFragment
import me.alex.pet.apps.zhishi.presentation.rules.RulesFragment
import me.alex.pet.apps.zhishi.presentation.rules.RulesToDisplay
import me.alex.pet.apps.zhishi.presentation.search.SearchFragment
import me.alex.pet.apps.zhishi.presentation.section.SectionFragment

object AppScreens {

    fun contents() = FragmentScreen { ContentsFragment.newInstance() }

    fun section(sectionId: Long) = FragmentScreen { SectionFragment.newInstance(sectionId) }

    fun rules(
            rulesToDisplay: RulesToDisplay,
            displaySectionButton: Boolean = false
    ) = FragmentScreen { RulesFragment.newInstance(rulesToDisplay, displaySectionButton) }

    fun search() = FragmentScreen { SearchFragment.newInstance() }
}