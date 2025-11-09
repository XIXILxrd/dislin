@file:OptIn(KordPreview::class)

package dev.rukhlovar

import dev.kord.common.annotation.KordPreview
import dev.kord.common.annotation.KordVoice
import dev.kord.core.Kord
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.core.gateway.handler.DefaultGatewayEventInterceptor
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.rukhlovar.commands.Command
import dev.rukhlovar.commands.ConnectionCommand
import dev.rukhlovar.commands.MessagingCommand
import dev.rukhlovar.commands.MusicPlayedCommand

@OptIn(KordVoice::class)
suspend fun main() {
    val token = System.getProperty("bot.token", null) ?: error("Token for bot must be passed in local.properties")

    val commands: List<Command> = listOf(
        ConnectionCommand.Join,
        ConnectionCommand.Leave,
        MessagingCommand.Send,
        MessagingCommand.Edit,
        MessagingCommand.Remove,
        MusicPlayedCommand.Play,
        MusicPlayedCommand.Stop,
        MusicPlayedCommand.Remove,
        MusicPlayedCommand.Add,
        MusicPlayedCommand.Skip,
    )

    val voiceConnectionRepository = VoiceConnectionRepositoryImpl

    val kord = Kord(token) {
        gatewayEventInterceptor = DefaultGatewayEventInterceptor(
            customContextCreator = { _, _ -> voiceConnectionRepository }
        )
    }

    val commandRegistry = CommandRegistry(kord)
    commandRegistry.register(commands)

    kord.on<ChatInputCommandInteractionCreateEvent> {
        this.customContext
        commandRegistry.handleEvent(this)
    }

    kord.login {
        @OptIn(PrivilegedIntent::class)
        intents += Intent.MessageContent
    }
}
