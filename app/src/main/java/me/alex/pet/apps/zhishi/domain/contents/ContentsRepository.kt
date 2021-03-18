package me.alex.pet.apps.zhishi.domain.contents

interface ContentsRepository {

    suspend fun getContents(): Contents
}