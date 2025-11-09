package dev.rukhlovar

import dev.kord.common.annotation.KordVoice
import dev.kord.common.entity.Snowflake
import dev.kord.voice.VoiceConnection

@KordVoice
interface VoiceConnectionRepository {

    suspend fun get(guildIdentifier: Snowflake): VoiceConnection?

    suspend fun put(guildIdentifier: Snowflake, voiceConnection: VoiceConnection)

    suspend fun remove(guildIdentifier: Snowflake)
}