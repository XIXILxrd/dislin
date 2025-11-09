package dev.rukhlovar.event_handlers

import dev.kord.common.annotation.KordPreview
import dev.kord.common.annotation.KordVoice
import dev.kord.core.behavior.channel.connect
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.rukhlovar.VoiceConnectionRepository

@KordPreview
@KordVoice
class ConnectionEventHandler : Event.Connection {

    override suspend fun join(event: ChatInputCommandInteractionCreateEvent) {
        val interaction = event.interaction

        val response = interaction.deferPublicResponse()

        val guildIdentifier = interaction.data.guildId.value ?: return
        val guild = interaction.kord.getGuild(guildIdentifier)
        val member = guild.getMemberOrNull(interaction.user.id) ?: return

        val memberVoiceState = member.getVoiceStateOrNull() ?: run {
            response.respond { content = "❌ Вы должны находиться в голосовом канале" }
            return
        }

        memberVoiceState.getChannelOrNull()?.let { channel ->
            val voiceConnectionRepository = event.customContext as VoiceConnectionRepository

            voiceConnectionRepository.get(guildIdentifier)?.let {
                val botVoiceChannelId = event.kord.getSelf().asMember(guildIdentifier).getVoiceState().channelId
                if (channel.id == botVoiceChannelId) {
                    response.respond { content = "✅ Уже подключен к вашему голосовому каналу" }
                    return
                }
            }

            channel.connect { selfDeaf = true }.also { voiceConnectionRepository.put(guildIdentifier, it) }
            response.respond { content = "✅ Подключился к вашему голосовому каналу" }
        }
    }

    override suspend fun leave(event: ChatInputCommandInteractionCreateEvent) {
        val interaction = event.interaction

        val response = interaction.deferPublicResponse()
        val guildIdentifier = interaction.data.guildId.value ?: return

        val voiceConnectionRepository = event.customContext as VoiceConnectionRepository

        val currentVoiceConnection = voiceConnectionRepository.get(guildIdentifier) ?: run {
            response.respond { content = "❌ Бот должен находиться в голосовом канале" }
            return
        }

        currentVoiceConnection.shutdown()
        voiceConnectionRepository.remove(guildIdentifier)
        response.respond { content = "✅ Отключился от голосовых каналов" }
    }
}