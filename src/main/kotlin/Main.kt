@file:OptIn(KordPreview::class)

package dev.rukhlovar

import dev.kord.common.annotation.KordPreview
import dev.kord.common.annotation.KordVoice
import dev.kord.core.Kord
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.ComponentInteractionCreateEvent
import dev.kord.core.event.interaction.SelectMenuInteractionCreateEvent
import dev.kord.core.gateway.handler.DefaultGatewayEventInterceptor
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.rukhlovar.commands.Command
import dev.rukhlovar.commands.ConnectionCommand
import dev.rukhlovar.commands.MusicPlayedCommand
import dev.rukhlovar.event_handlers.ConnectionEventHandler
import dev.rukhlovar.event_handlers.MusicPlayerEventHandler
import dev.rukhlovar.event_handlers.SelectMenuListener
import dev.rukhlovar.services.voice_connection.VoiceConnectionService
import dev.rukhlovar.services.voice_connection.VoiceConnectionServiceImpl
import dev.rukhlovar.services.CommandRegistry
import dev.rukhlovar.services.music.MusicService
import dev.rukhlovar.services.music.PlayerManager
import dev.schlaubi.lavakord.kord.lavakord
import dev.schlaubi.lavakord.plugins.lavasrc.LavaSrc
import kotlin.time.Duration.Companion.seconds

@OptIn(KordVoice::class)
suspend fun main() {
    val token = System.getProperty("bot.token", null) ?: error("Token for bot must be passed in local.properties")
    val address = System.getProperty("bot.address", null) ?: error("Lavalink address must be passed in local.properties")
    val password = System.getProperty("bot.pass", null) ?: error("Lavalink password must be passed in local.properties")

    val kord = Kord(token) {
        gatewayEventInterceptor = DefaultGatewayEventInterceptor(
            customContextCreator = { _, _ -> VoiceConnectionServiceImpl as VoiceConnectionService }
        )
    }
    val lavakord = kord.lavakord {
        link {
            autoReconnect = true
            retry = linear(2.seconds, 60.seconds, 10)
        }
        plugins {
            install(LavaSrc)
        }
    }.apply {
        addNode(
            serverUri = "ws://$address",
            password = password
        )
    }

    val playerManager = PlayerManager(lavakord)
    val musicService = MusicService(playerManager)
    val commandRegistry = CommandRegistry(kord)
    val selectMenuListener = SelectMenuListener(musicService)

    val connectionHandler = ConnectionEventHandler()
    val musicPlayerEventHandler = MusicPlayerEventHandler(musicService)

    val commands: List<Command> = listOf(
        //--------------------------------------------------------------------------------------------------------------
        // Connection

        ConnectionCommand.Join(connectionHandler),
        ConnectionCommand.Leave(connectionHandler),

        //--------------------------------------------------------------------------------------------------------------
        // Music

        MusicPlayedCommand.Play(musicPlayerEventHandler),
        MusicPlayedCommand.Stop(musicPlayerEventHandler),
        MusicPlayedCommand.Remove(musicPlayerEventHandler),
        MusicPlayedCommand.Skip(musicPlayerEventHandler),
    )
    commandRegistry.register(commands)

    kord.on<ChatInputCommandInteractionCreateEvent> {
        commandRegistry.handleEvent(this)
    }

    kord.on<SelectMenuInteractionCreateEvent> {
        selectMenuListener.handle(this)
    }

    kord.on<ReadyEvent> {
        println("Bot started")
    }

    kord.login {
        @OptIn(PrivilegedIntent::class)
        intents += Intent.MessageContent
        intents += Intent.GuildVoiceStates
    }
}
