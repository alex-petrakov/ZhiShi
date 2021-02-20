package me.alex.pet.apps.zhishi.domain

import me.alex.pet.apps.zhishi.domain.contents.Contents

interface RulesRepository {

    suspend fun getContents(): Contents
}