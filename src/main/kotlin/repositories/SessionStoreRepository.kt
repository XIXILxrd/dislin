package dev.rukhlovar.repositories

import dev.arbjerg.lavalink.protocol.v4.Track
import dev.kord.common.entity.Snowflake
import io.ktor.util.collections.ConcurrentMap

object SessionStoreRepository {
    private val data = ConcurrentMap<Snowflake, List<Track>>()

    fun save(userId: Snowflake, value: List<Track>) {
        data[userId] = value
    }

    fun get(userId: Snowflake): List<Track>? {
        return data[userId]
    }

    fun clear(userId: Snowflake) {
        data.remove(userId)
    }
}