package dev.rukhlovar.commands

import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.BaseInputChatBuilder
import dev.kord.rest.builder.interaction.integer
import dev.kord.rest.builder.interaction.string
import dev.rukhlovar.event_handlers.MusicPlayerEventHandler

sealed class MusicPlayedCommand(
    override val name: String,
    override val description: String
) : Command(name, description) {

    val musicPlayerEventHandler = MusicPlayerEventHandler()

    object Play : MusicPlayedCommand("play", "Попросить бота включить трек") {
        private const val PARAM_NAME = "query"
        private const val PARAM_DESCRIPTION = "Название трека"

        override suspend fun configure(builder: BaseInputChatBuilder) {
            builder.string(PARAM_NAME, PARAM_DESCRIPTION)
        }

        override suspend fun execute(event: ChatInputCommandInteractionCreateEvent) {
            musicPlayerEventHandler.play()
        }
    }

    object Stop : MusicPlayedCommand("stop", "Попросить бота выключить трек") {
        override suspend fun configure(builder: BaseInputChatBuilder) { /*nothing*/ }

        override suspend fun execute(event: ChatInputCommandInteractionCreateEvent) {
            musicPlayerEventHandler.stop()
        }
    }

    object Skip : MusicPlayedCommand("skip", "Попросить бота пропустить текущий трек") {
        override suspend fun configure(builder: BaseInputChatBuilder) { /*nothing*/ }

        override suspend fun execute(event: ChatInputCommandInteractionCreateEvent) {
            musicPlayerEventHandler.skip()
        }
    }

    object Add : MusicPlayedCommand("add", "Попросить добавить бота трек в очередь") {
        private const val PARAM_NAME = "query"
        private const val PARAM_DESCRIPTION = "Название трека"

        override suspend fun configure(builder: BaseInputChatBuilder) {
            builder.string(PARAM_NAME, PARAM_DESCRIPTION)
        }

        override suspend fun execute(event: ChatInputCommandInteractionCreateEvent) {
            musicPlayerEventHandler.add()
        }
    }

    object Remove : MusicPlayedCommand("remove", "Попросить бота удалить трек из очереди") {
        private const val PARAM_NAME = "position"
        private const val PARAM_DESCRIPTION = "Номер трека"

        override suspend fun configure(builder: BaseInputChatBuilder) {
            builder.integer(PARAM_NAME, PARAM_DESCRIPTION)
        }

        override suspend fun execute(event: ChatInputCommandInteractionCreateEvent) {
            musicPlayerEventHandler.remove()
        }
    }
}