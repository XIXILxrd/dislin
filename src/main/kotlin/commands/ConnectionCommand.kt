@file:OptIn(KordVoice::class)

package dev.rukhlovar.commands

import dev.kord.common.annotation.KordVoice
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.BaseInputChatBuilder
import dev.rukhlovar.event_handlers.ConnectionEventHandler

sealed class ConnectionCommand(override val name: String, override val description: String) : Command(name, description) {

    val connectionEventHandler = ConnectionEventHandler()

    object Join : ConnectionCommand("join", "Пригласить бота в голосовой чат") {
        override suspend fun configure(builder: BaseInputChatBuilder) { /*nothing*/ }

        override suspend fun execute(event: ChatInputCommandInteractionCreateEvent) {
            connectionEventHandler.join(event)
        }
    }

    object Leave : ConnectionCommand("leave", "Выгнать бота из голосового чата") {
        override suspend fun configure(builder: BaseInputChatBuilder) { /*nothing*/ }

        override suspend fun execute(event: ChatInputCommandInteractionCreateEvent) {
            connectionEventHandler.leave(event)
        }
    }
}