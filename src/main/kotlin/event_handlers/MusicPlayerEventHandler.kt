package dev.rukhlovar.event_handlers

import dev.kord.common.annotation.KordPreview
import dev.kord.common.annotation.KordVoice
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.rest.builder.component.actionRow
import dev.kord.rest.builder.component.option
import dev.kord.rest.builder.message.embed
import dev.rukhlovar.commands.MusicPlayedCommand
import dev.rukhlovar.models.SelectMenu
import dev.rukhlovar.models.TrackSource
import dev.rukhlovar.repositories.SessionStoreRepository
import dev.rukhlovar.services.voice_connection.VoiceConnectionService
import dev.rukhlovar.services.music.MusicService

@OptIn(KordVoice::class)
class MusicPlayerEventHandler(
    private val musicService: MusicService,
    private val connectionRepository: VoiceConnectionService
) : Event.MusicPlayer {

    @OptIn(KordPreview::class)
    override suspend fun play(interaction: ChatInputCommandInteraction) {
        val deferred = interaction.deferPublicResponse()

        val query = interaction.command.strings[MusicPlayedCommand.Play.QUERY_PARAM]!! // always non-null
        val source = interaction.command.strings[MusicPlayedCommand.Play.SOURCE_PARAM] ?: TrackSource.YOUTUBE.prefix
        val fullQuery = "$source:$query"

        val guild = interaction.getGuild()
        val searchResult = musicService.search(guild.id, fullQuery)

        if (searchResult.isEmpty()) {
            interaction.sendMessage("❌ Ничего не найдено")
            return
        }

        deferred.respond {
            content = "🔍 Результаты поиска:"

            actionRow {
                stringSelect(SelectMenu.TRACK_SELECT.value) {
                    searchResult.forEachIndexed { index, track ->
                        option("${index + 1}. ${track.info.title}", value = index.toString())
                    }
                }
            }
        }

        SessionStoreRepository.save(interaction.user.id, searchResult)
        connectionRepository.join(interaction)
    }

    @OptIn(KordPreview::class)
    override suspend fun stop(interaction: ChatInputCommandInteraction) {
        val deferred = interaction.deferPublicResponse()
        val guildId = interaction.getGuild().id

        musicService.stop(guildId)

        deferred.respond { content = "Трек остановлен" }
    }

    @OptIn(KordPreview::class)
    override suspend fun skip(interaction: ChatInputCommandInteraction) {
        val response = interaction.deferPublicResponse()
        val guildId = interaction.getGuild().id

        val skippedTrack = musicService.skip(guildId)

        if (skippedTrack == null) {
            response.respond { content = "В очереди нет треков" }
            return
        }
        response.respond { content = "Трек ${skippedTrack.info.title} был пропущен пропущен" }
    }

    override suspend fun remove(interaction: ChatInputCommandInteraction) {
        val deferred = interaction.deferPublicResponse()
        val guildId = interaction.getGuild().id

        val queue = musicService.getQueue(guildId)

        deferred.respond {
            embed {
                title = "Список треков в очереди"
                description = "Выбери трек из списка"
            }

            actionRow {
                stringSelect(SelectMenu.TRACK_REMOVE.value) {
                    queue.forEachIndexed { index, track ->
                        option(track.info.title, value = index.toString())
                    }
                }
            }
        }

        SessionStoreRepository.save(interaction.user.id, queue)
    }

    override suspend fun queue(interaction: ChatInputCommandInteraction) {
        val deferred = interaction.deferPublicResponse()
        val guildId = interaction.getGuild().id

        val queue = musicService.getQueue(guildId)

        deferred.respond {
            embed {
                title = "Список треков в очереди:"

                queue.forEachIndexed { index, track ->
                    field {
                        value = "${index + 1}. ${track.info.title}"
                    }
                }
            }
        }
    }
}

suspend fun ChatInputCommandInteraction.getGuild(): Guild = data.guildId.value!!.let { kord.getGuild(it) }
suspend fun ChatInputCommandInteraction.getMember(): Member =
    data.member.value!!.let { getGuild().getMember(it.userId) }

suspend fun ChatInputCommandInteraction.getMemberChannelId(): Snowflake? {
    val voiceState = getMember().getVoiceStateOrNull()
    return voiceState?.channelId
}

suspend fun ChatInputCommandInteraction.sendMessage(text: String) = deferPublicResponse().respond { content = text }