package dev.rukhlovar.services.music

import dev.arbjerg.lavalink.protocol.v4.Track
import dev.kord.common.entity.Snowflake

class MusicService(
    private val playerManager: PlayerManager
) {

    suspend fun enqueue(guildId: Snowflake, track: Track) {
        val player = playerManager.getOrCreate(guildId)

        println("enqueue: $track")
        player.enqueue(track)
    }

    suspend fun skip(guildId: Snowflake): Track? {
        val player = playerManager.getOrCreate(guildId)

        return player.skip()
    }

    suspend fun stop(guildId: Snowflake) {
        val player = playerManager.getOrCreate(guildId)

        player.stop()
    }

    suspend fun remove(guildId: Snowflake, position: Int): Track? {
        val player = playerManager.getOrCreate(guildId)

        return player.removeTrackAt(position)
    }

    suspend fun getQueue(guildId: Snowflake): List<Track> {
        val player = playerManager.getOrCreate(guildId)

        return player.queue()
    }

    suspend fun search(guildId: Snowflake, query: String): List<Track> {
        val player = playerManager.getOrCreate(guildId)

        return player.search(query, MAX_RESULT_LENGTH)
    }

    suspend fun connectAudio(guildId: Snowflake, voiceChannelId: Snowflake) {
        val player = playerManager.getOrCreate(guildId)
        player.connectAudio(voiceChannelId)
    }

    companion object {
        private const val MAX_RESULT_LENGTH = 10
    }
}