package dev.rukhlovar.commands

import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.BaseInputChatBuilder

abstract class Command(open val name: String, open val description: String) {
    abstract suspend fun configure(builder: BaseInputChatBuilder)

    abstract suspend fun execute(event: ChatInputCommandInteractionCreateEvent)
}
