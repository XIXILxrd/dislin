package dev.rukhlovar.event_handlers

import dev.kord.common.annotation.KordPreview
import dev.kord.common.annotation.KordVoice
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.rukhlovar.models.ConnectionResult
import dev.rukhlovar.repositories.voice_connection.VoiceConnectionRepository

@KordPreview
@KordVoice
class ConnectionEventHandler : Event.Connection {

    override suspend fun join(event: ChatInputCommandInteractionCreateEvent) {
        val response = event.interaction.deferPublicResponse()
        val voiceConnectionRepository = event.customContext as VoiceConnectionRepository

        val connectionState = voiceConnectionRepository.join(event)

        when (connectionState) {
            is ConnectionResult.Disconnected -> response.respond { content = "❌ Бот должен находиться в голосовом канале" }
            is ConnectionResult.Connected -> response.respond { content = "Бот подключился к комнате" }
            else -> response.respond { content = "❌ Что-то пошло не так" }
        }
    }

    override suspend fun leave(event: ChatInputCommandInteractionCreateEvent) {
        val response = event.interaction.deferPublicResponse()
        val voiceConnectionRepository = event.customContext as VoiceConnectionRepository

        val connectionState = voiceConnectionRepository.leave(event)

        when (connectionState) {
            is ConnectionResult.Connected -> response.respond { content = "❌ Что-то пошло не так" }
            is ConnectionResult.Disconnected -> response.respond { content = "Бот вышел из комнаты" }
            else -> response.respond { content = "❌ Что-то пошло не так" }
        }
    }
}