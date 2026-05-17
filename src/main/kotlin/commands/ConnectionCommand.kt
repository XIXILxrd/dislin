@file:OptIn(KordVoice::class)

package dev.rukhlovar.commands

import dev.kord.common.annotation.KordPreview
import dev.kord.common.annotation.KordVoice
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.BaseInputChatBuilder
import dev.rukhlovar.event_handlers.Event

@OptIn(KordPreview::class)
sealed class ConnectionCommand(
    override val name: String,
    override val description: String
) : Command(name, description) {

    class Join(
        private val connectionEventHandler: Event.Connection
    ) : ConnectionCommand("join", "Пригласить бота в голосовой чат") {
        override suspend fun configure(builder: BaseInputChatBuilder) { /*nothing*/ }

        @OptIn(KordPreview::class)
        override suspend fun execute(event: ChatInputCommandInteractionCreateEvent) {
            connectionEventHandler.join(event)
        }
    }

    class Leave(
        private val connectionEventHandler: Event.Connection
    ) : ConnectionCommand("leave", "Выгнать бота из голосового чата") {
        override suspend fun configure(builder: BaseInputChatBuilder) { /*nothing*/ }

        override suspend fun execute(event: ChatInputCommandInteractionCreateEvent) {
            connectionEventHandler.leave(event)
        }
    }
}