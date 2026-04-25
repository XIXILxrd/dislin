package dev.rukhlovar.repositories.voice_connection

import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.rukhlovar.models.ConnectionResult

interface VoiceConnectionRepository {

    suspend fun join(event: ChatInputCommandInteractionCreateEvent): ConnectionResult

    suspend fun leave(event: ChatInputCommandInteractionCreateEvent): ConnectionResult
}