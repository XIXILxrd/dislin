@file:OptIn(KordPreview::class)

package dev.rukhlovar

import dev.kord.common.annotation.KordPreview
import dev.kord.common.annotation.KordVoice
import dev.kord.core.Kord
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.core.gateway.handler.DefaultGatewayEventInterceptor
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.rukhlovar.commands.Command
import dev.rukhlovar.commands.ConnectionCommand
import dev.rukhlovar.commands.MessagingCommand
import dev.rukhlovar.commands.MusicPlayedCommand
import dev.rukhlovar.repositories.voice_connection.VoiceConnectionRepository
import dev.rukhlovar.repositories.voice_connection.VoiceConnectionRepositoryImpl
import dev.schlaubi.lavakord.kord.lavakord
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.seconds

@OptIn(KordVoice::class)
fun main() = runBlocking {
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

    val kord = Kord(token) {
        gatewayEventInterceptor = DefaultGatewayEventInterceptor(
            customContextCreator = { _, _ -> VoiceConnectionRepositoryImpl as VoiceConnectionRepository }
        )
    }

    kord.lavakord {
        link {
            autoReconnect = true
            retry = linear(2.seconds, 60.seconds, 10)
        }
    }.apply {
        addNode(
            serverUri = "ws://0.0.0.0:2333",
            password = "youshallnotpass"
        )
    }

    val commandRegistry = CommandRegistry(kord)
    commandRegistry.register(commands)

    kord.on<ChatInputCommandInteractionCreateEvent> {
        commandRegistry.handleEvent(this)
    }

    kord.on<ReadyEvent> {
        println("Bot started")
    }

    kord.login {
        @OptIn(PrivilegedIntent::class)
        intents += Intent.MessageContent
    }
}
