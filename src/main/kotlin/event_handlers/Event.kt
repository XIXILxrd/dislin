package dev.rukhlovar.event_handlers

import dev.kord.common.annotation.KordVoice
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.voice.VoiceConnection

interface Event {
    interface MusicPlayer: Event {
        suspend fun play()

        suspend fun stop()

        suspend fun skip()

        suspend fun add()

        suspend fun remove()
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