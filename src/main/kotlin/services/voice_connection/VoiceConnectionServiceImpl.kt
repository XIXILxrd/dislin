package dev.rukhlovar.services.voice_connection

import dev.kord.common.annotation.KordVoice
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.connect
import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.voice.VoiceConnection
import dev.rukhlovar.models.ConnectionResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@KordVoice
object VoiceConnectionServiceImpl : VoiceConnectionService {

    private val _connections = MutableStateFlow<Map<Snowflake, VoiceConnection>>(emptyMap())

    private fun get(guildIdentifier: Snowflake): VoiceConnection? {
        return _connections.value[guildIdentifier]
    }

    private fun put(guildIdentifier: Snowflake, voiceConnection: VoiceConnection) {
        _connections.update { it + (guildIdentifier to voiceConnection) }
    }

    private fun remove(guildIdentifier: Snowflake) {
        _connections.update { it - guildIdentifier }
    }

    override suspend fun join(interaction: ChatInputCommandInteraction): ConnectionResult {
        val guildIdentifier = interaction.data.guildId.value ?: return ConnectionResult.Disconnected
        val guild = interaction.kord.getGuild(guildIdentifier)
        val member = guild.getMemberOrNull(interaction.user.id) ?: return ConnectionResult.Disconnected

        val memberVoiceState = member.getVoiceStateOrNull() ?: run {
            return ConnectionResult.Error
        }

        memberVoiceState.getChannelOrNull()?.let { channel ->
            get(guildIdentifier)?.let {
                val botVoiceChannelId = interaction.kord.getSelf().asMember(guildIdentifier).getVoiceState().channelId
                if (channel.id == botVoiceChannelId) {
                    return ConnectionResult.Error
                }
            }

            val voiceState = channel.connect { } .also {
                put(guildIdentifier, voiceConnection = it)
            }

            return ConnectionResult.Connected(voiceState)
        }

        return ConnectionResult.Disconnected
    }

    override suspend fun leave(interaction: ChatInputCommandInteraction): ConnectionResult {
        val guildIdentifier = interaction.data.guildId.value ?: return ConnectionResult.Error
        val currentVoiceConnection = get(guildIdentifier) ?: return ConnectionResult.Error

        currentVoiceConnection.leave().also {
            remove(guildIdentifier)
        }
        return ConnectionResult.Disconnected
    }
}