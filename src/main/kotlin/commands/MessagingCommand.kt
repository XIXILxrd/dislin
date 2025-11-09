package dev.rukhlovar.commands

import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.BaseInputChatBuilder
import dev.kord.rest.builder.interaction.string
import dev.rukhlovar.event_handlers.MessagingEventHandler

sealed class MessagingCommand(
    override val name: String,
    override val description: String
) : Command(name, description) {

    val messagingEventHandler = MessagingEventHandler()

    object Send : MessagingCommand("send", "Попросить бота отправить сообщение с текстом") {
        private const val PARAM_NAME = "message"
        private const val PARAM_DESCRIPTION = "Сообщение, которое нужно отправить"

        override suspend fun configure(builder: BaseInputChatBuilder) {
            builder.string(PARAM_NAME, PARAM_DESCRIPTION)
        }

        override suspend fun execute(event: ChatInputCommandInteractionCreateEvent) {
//            TODO
//            messagingEventHandler.send()
        }

    }

    object Remove : MessagingCommand("remove", "Попросить бота удалить сообщение") {
        override suspend fun configure(builder: BaseInputChatBuilder) { /*nothing*/ }

        override suspend fun execute(event: ChatInputCommandInteractionCreateEvent) {
            messagingEventHandler.remove()
        }

    }

    object Edit : MessagingCommand("edit", "Попросить бота отредактировать сообщение") {
        override suspend fun configure(builder: BaseInputChatBuilder) { }

        override suspend fun execute(event: ChatInputCommandInteractionCreateEvent) {
//            TODO
//            messagingEventHandler.edit()
        }

    }
}