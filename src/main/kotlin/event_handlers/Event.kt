package dev.rukhlovar.event_handlers

import dev.kord.common.annotation.KordVoice
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent

interface Event {
    interface MusicPlayer : Event {
        suspend fun play(event: ChatInputCommandInteractionCreateEvent)

        suspend fun stop(event: ChatInputCommandInteractionCreateEvent)

        suspend fun skip(event: ChatInputCommandInteractionCreateEvent)

        suspend fun add(event: ChatInputCommandInteractionCreateEvent)

        suspend fun remove(event: ChatInputCommandInteractionCreateEvent)
    }

    @KordVoice
    interface Connection: Event {
        suspend fun join(event: ChatInputCommandInteractionCreateEvent)

        suspend fun leave(event: ChatInputCommandInteractionCreateEvent)
    }

    interface Messaging: Event {
        suspend fun send(content: String)

        suspend fun remove()

        suspend fun edit(content: String)
    }
}