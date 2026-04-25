package dev.rukhlovar.commands

import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.BaseInputChatBuilder
import dev.kord.rest.builder.interaction.integer
import dev.kord.rest.builder.interaction.string
import dev.rukhlovar.event_handlers.MusicPlayerEventHandler
import dev.rukhlovar.models.TrackSource

sealed class MusicPlayedCommand(
    override val name: String,
    override val description: String
) : Command(name, description) {

    val musicPlayerEventHandler = MusicPlayerEventHandler()

    object Play : MusicPlayedCommand("play", "Попросить бота включить трек") {
        const val QUERY_PARAM = "query"
        private const val QUERY_PARAM_DESCRIPTION = "Название трека"
        const val SOURCE_PARAM = "source"
        private const val SOURCE_PARAM_DESCRIPTION = "Где искать трек"

        override suspend fun configure(builder: BaseInputChatBuilder) {
            builder.string(QUERY_PARAM, QUERY_PARAM_DESCRIPTION) {
                required = true
            }
            builder.string(SOURCE_PARAM, SOURCE_PARAM_DESCRIPTION) {
                required = false
                TrackSource.entries.forEach { source ->
                    choice(source.title, source.prefix)
                }
            }
        }

        override suspend fun execute(event: ChatInputCommandInteractionCreateEvent) {
            musicPlayerEventHandler.play(event)
        }
    }

    object Stop : MusicPlayedCommand("stop", "Попросить бота выключить трек") {
        override suspend fun configure(builder: BaseInputChatBuilder) { /*nothing*/ }

        override suspend fun execute(event: ChatInputCommandInteractionCreateEvent) {
            musicPlayerEventHandler.stop(event)
        }
    }

    object Skip : MusicPlayedCommand("skip", "Попросить бота пропустить текущий трек") {
        override suspend fun configure(builder: BaseInputChatBuilder) { /*nothing*/ }

        override suspend fun execute(event: ChatInputCommandInteractionCreateEvent) {
            musicPlayerEventHandler.skip(event)
        }
    }

    object Add : MusicPlayedCommand("add", "Попросить добавить бота трек в очередь") {
        const val QUERY_PARAM = "query"
        private const val QUERY_PARAM_DESCRIPTION = "Название трека"
        const val SOURCE_PARAM = "source"
        private const val SOURCE_PARAM_DESCRIPTION = "Где искать трек"

        override suspend fun configure(builder: BaseInputChatBuilder) {
            builder.string(QUERY_PARAM, QUERY_PARAM_DESCRIPTION) {
                required = true
            }
            builder.string(SOURCE_PARAM, SOURCE_PARAM_DESCRIPTION) {
                required = false
                TrackSource.entries.forEach { source ->
                    choice(source.title, source.prefix)
                }
            }
        }

        override suspend fun execute(event: ChatInputCommandInteractionCreateEvent) {
            musicPlayerEventHandler.add(event)
        }
    }

    object Remove : MusicPlayedCommand("remove", "Попросить бота удалить трек из очереди") {
        private const val PARAM_NAME = "position"
        private const val PARAM_DESCRIPTION = "Номер трека"

        override suspend fun configure(builder: BaseInputChatBuilder) {
            builder.integer(PARAM_NAME, PARAM_DESCRIPTION)
        }

        override suspend fun execute(event: ChatInputCommandInteractionCreateEvent) {
            musicPlayerEventHandler.remove(event)
        }
    }
}