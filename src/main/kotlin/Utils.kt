package dev.rukhlovar

import dev.kord.common.annotation.KordVoice
import dev.kord.core.behavior.channel.BaseVoiceChannelBehavior
import dev.kord.core.exception.GatewayNotFoundException
import dev.kord.voice.VoiceConnection
import dev.kord.voice.VoiceConnectionBuilder

@KordVoice
suspend fun BaseVoiceChannelBehavior.disconnect(builder: VoiceConnectionBuilder.() -> Unit): VoiceConnection {
    val voiceConnection = VoiceConnection(
        guild.gateway ?: GatewayNotFoundException.voiceConnectionGatewayNotFound(guildId),
        kord.selfId,
        id,
        guildId,
        builder
    )
    voiceConnection.leave()

    return voiceConnection
}
