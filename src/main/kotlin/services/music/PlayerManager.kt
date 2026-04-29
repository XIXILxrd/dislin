package dev.rukhlovar.services.music

import dev.kord.common.entity.Snowflake
import dev.schlaubi.lavakord.LavaKord
import dev.schlaubi.lavakord.audio.TrackEndEvent
import dev.schlaubi.lavakord.audio.TrackStartEvent
import dev.schlaubi.lavakord.audio.on
import dev.schlaubi.lavakord.kord.getLink

class PlayerManager(
    private val lavakord: LavaKord
) {
    private val players = mutableMapOf<Snowflake, GuildPlayer>()

    init {
        subscribeToTrackStartEvent()
        subscribeToTrackEndEvent()
    }

    fun getOrCreate(guildId: Snowflake): GuildPlayer = players.getOrPut(guildId) {
        GuildPlayer(lavakord.getLink(guildId))
    }

    fun remove(guildId: Snowflake) {
        players.remove(guildId)
    }

    fun subscribeToTrackEndEvent() {
        lavakord.on<TrackEndEvent> {
            val player = players[Snowflake(guildId)] ?: return@on
            player.onTrackEnd(this)
        }
    }

    fun subscribeToTrackStartEvent() {
        lavakord.on<TrackStartEvent> {
            val player = players[Snowflake(guildId)] ?: return@on
            player.onTrackStart(this)
        }
    }

}
