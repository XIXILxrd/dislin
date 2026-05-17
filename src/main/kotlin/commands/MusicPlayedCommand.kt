package dev.rukhlovar.commands

import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.BaseInputChatBuilder
import dev.kord.rest.builder.interaction.integer
import dev.kord.rest.builder.interaction.string
import dev.rukhlovar.event_handlers.Event
import dev.rukhlovar.models.TrackSource

sealed class MusicPlayedCommand(
    override val name: String,
    override val description: String
) : Command(name, description) {

    class Play(
        private val musicPlayerEventHandler: Event.MusicPlayer
    ) : MusicPlayedCommand("play", "Попросить бота включить трек") {

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
            musicPlayerEventHandler.play(event.interaction)
        }

        companion object {
            const val QUERY_PARAM = "query"
            private const val QUERY_PARAM_DESCRIPTION = "Название трека"
            const val SOURCE_PARAM = "source"
            private const val SOURCE_PARAM_DESCRIPTION = "Где искать трек"
        }
    }

    class Stop(
        private val musicPlayerEventHandler: Event.MusicPlayer
    ) : MusicPlayedCommand("stop", "Попросить бота выключить трек") {
        override suspend fun configure(builder: BaseInputChatBuilder) { /*nothing*/ }

        override suspend fun execute(event: ChatInputCommandInteractionCreateEvent) {
            musicPlayerEventHandler.stop(event.interaction)
        }
    }

    class Skip(
        private val musicPlayerEventHandler: Event.MusicPlayer
    ) : MusicPlayedCommand("skip", "Попросить бота пропустить текущий трек") {
        override suspend fun configure(builder: BaseInputChatBuilder) { /*nothing*/ }

        override suspend fun execute(event: ChatInputCommandInteractionCreateEvent) {
            musicPlayerEventHandler.skip(event.interaction)
        }
    }

    class Remove(
        private val musicPlayerEventHandler: Event.MusicPlayer
    ) : MusicPlayedCommand("remove", "Попросить бота удалить трек из очереди") {

        override suspend fun configure(builder: BaseInputChatBuilder) {
            builder.integer(QUERY_PARAM, PARAM_DESCRIPTION)
        }

        override suspend fun execute(event: ChatInputCommandInteractionCreateEvent) {
            musicPlayerEventHandler.remove(event.interaction)
        }

        companion object {
            const val QUERY_PARAM = "position"
            private const val PARAM_DESCRIPTION = "Номер трека"
        }
    }
}