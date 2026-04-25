package dev.rukhlovar.repositories.music_player

import dev.kord.core.Kord

interface MusicPlayerRepository {
    suspend fun play(kord: Kord, query: String)

    suspend fun pause()

    suspend fun skip()

    suspend fun add()

    suspend fun queue()
}