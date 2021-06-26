package me.alex.pet.apps.zhishi.domain.rules

interface RulesRepository {

    suspend fun getRule(ruleId: Long): Rule?
}