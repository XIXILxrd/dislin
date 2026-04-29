package dev.rukhlovar.services.voice_connection

import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.rukhlovar.models.ConnectionResult

interface VoiceConnectionService {

    suspend fun join(interaction: ChatInputCommandInteraction): ConnectionResult

    suspend fun leave(interaction: ChatInputCommandInteraction): ConnectionResult
}