package me.alex.pet.apps.zhishi.domain

import me.alex.pet.apps.zhishi.domain.contents.Contents
import me.alex.pet.apps.zhishi.domain.rules.Rule
import me.alex.pet.apps.zhishi.domain.rules.Section

interface RulesRepository {

    suspend fun getContents(): Contents

    suspend fun getSection(sectionId: Long): Section?

    suspend fun getRule(ruleId: Long): Rule?
}