package dev.rukhlovar.event_handlers

import dev.kord.common.annotation.KordPreview
import dev.kord.common.annotation.KordVoice
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.rukhlovar.commands.MusicPlayedCommand
import dev.rukhlovar.models.ConnectionResult
import dev.rukhlovar.models.TrackSource
import dev.rukhlovar.repositories.music_player.MusicPlayerRepository
import dev.rukhlovar.repositories.music_player.MusicPlayerRepositoryImpl
import dev.rukhlovar.repositories.voice_connection.VoiceConnectionRepository

@OptIn(KordVoice::class)
class MusicPlayerEventHandler : Event.MusicPlayer {

    private val musicPlayerRepository: MusicPlayerRepository = MusicPlayerRepositoryImpl()

    @OptIn(KordPreview::class)
    override suspend fun play(event: ChatInputCommandInteractionCreateEvent) {
        val interaction = event.interaction
        val voiceConnectionRepository = event.customContext as VoiceConnectionRepository

        val response = interaction.deferPublicResponse()
        val command = interaction.command

        val query = command.strings[MusicPlayedCommand.Play.QUERY_PARAM]!! // always non-null
        val source = command.strings[MusicPlayedCommand.Play.SOURCE_PARAM] ?: TrackSource.YOUTUBE.prefix

        val currentVoiceConnection = voiceConnectionRepository.join(event) as? ConnectionResult.Connected

        if (currentVoiceConnection == null) {
            response.respond { content = "❌ Что-то пошло не так" }
            return
        } else {
            response.respond { content = "я сосал" }
        }

        response.respond { content = "Сейчас играет: $query" }
    }

    override suspend fun stop(event: ChatInputCommandInteractionCreateEvent) {
        TODO("Not yet implemented")
    }

    override suspend fun skip(event: ChatInputCommandInteractionCreateEvent) {
        TODO("Not yet implemented")
    }

    override suspend fun add(event: ChatInputCommandInteractionCreateEvent) {
        TODO("Not yet implemented")
    }

    override suspend fun remove(event: ChatInputCommandInteractionCreateEvent) {
        TODO("Not yet implemented")
    }
}