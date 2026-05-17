package dev.rukhlovar.event_handlers

import dev.kord.common.annotation.KordVoice
import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent

interface Event {
    interface MusicPlayer : Event {
        suspend fun play(interaction: ChatInputCommandInteraction)

        suspend fun stop(interaction: ChatInputCommandInteraction)

        suspend fun skip(interaction: ChatInputCommandInteraction)

        suspend fun remove(interaction: ChatInputCommandInteraction)

        suspend fun queue(interaction: ChatInputCommandInteraction)
    }

    @KordVoice
    interface Connection: Event {
        suspend fun join(event: ChatInputCommandInteractionCreateEvent)

        suspend fun leave(event: ChatInputCommandInteractionCreateEvent)
    }
}