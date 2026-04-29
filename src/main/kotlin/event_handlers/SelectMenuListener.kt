package dev.rukhlovar.event_handlers

import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.interaction.SelectMenuInteraction
import dev.kord.core.event.interaction.SelectMenuInteractionCreateEvent
import dev.rukhlovar.models.SelectMenu
import dev.rukhlovar.repositories.SessionStoreRepository
import dev.rukhlovar.services.music.MusicService

class SelectMenuListener(
    private val musicService: MusicService
) {
    suspend fun handle(event: SelectMenuInteractionCreateEvent) {
        val interaction = event.interaction

        when (interaction.componentId) {
            SelectMenu.TRACK_SELECT.value -> handleTrackSelection(interaction)

            SelectMenu.TRACK_REMOVE.value -> handleTrackRemoving(interaction)
            else -> return
        }
    }

    private suspend fun handleTrackSelection(interaction: SelectMenuInteraction) {
        val deferred = interaction.deferPublicResponse()

        val index = interaction.values.first().toInt().also {
            println("indexTrack: $it")
        }

        val tracks = SessionStoreRepository.get(interaction.user.id) ?: run {
            deferred.respond { content = "❌ Сессия устарела" }
            return
        }
        println("tracks:$tracks")

        val track = tracks.getOrNull(index) ?: run {
            deferred.respond { content = "❌ Ошибка выбора" }
            return
        }

        val guildId = interaction.data.guildId.value ?: run {
            println("не получилось guildId взять")
            return
        }
        musicService.enqueue(guildId, track)
        SessionStoreRepository.clear(interaction.user.id)
    }

    private suspend fun handleTrackRemoving(interaction: SelectMenuInteraction) {
        val deferred = interaction.deferPublicResponse()

        val index = interaction.values.first().toInt().also {
            println("indexTrack: $it")
        }

        val guildId = interaction.data.guildId.value ?: run {
            return
        }

        musicService.remove(guildId, index)
        SessionStoreRepository.clear(interaction.user.id)

        deferred.respond { content = "Трек был удален из очереди" }

    }
}