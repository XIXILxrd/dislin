package dev.rukhlovar

import dev.kord.common.annotation.KordVoice
import dev.kord.common.entity.Snowflake
import dev.kord.voice.VoiceConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@KordVoice
object VoiceConnectionRepositoryImpl : VoiceConnectionRepository {

    private val _connections = MutableStateFlow<Map<Snowflake, VoiceConnection>>(emptyMap())
    val connections = _connections.asStateFlow()

    override suspend fun get(guildIdentifier: Snowflake): VoiceConnection? {
        return _connections.value[guildIdentifier]
    }

    override suspend fun put(guildIdentifier: Snowflake, voiceConnection: VoiceConnection) {
        _connections.update { it + (guildIdentifier to voiceConnection) }
    }

    override suspend fun remove(guildIdentifier: Snowflake) {
        _connections.update {
            it[guildIdentifier]?.shutdown()
            it - guildIdentifier
        }
    }
}