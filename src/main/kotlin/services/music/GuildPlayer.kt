package dev.rukhlovar.services.music

import dev.arbjerg.lavalink.protocol.v4.LoadResult
import dev.arbjerg.lavalink.protocol.v4.Track
import dev.schlaubi.lavakord.audio.Link
import dev.schlaubi.lavakord.audio.TrackEndEvent
import dev.schlaubi.lavakord.audio.TrackStartEvent
import dev.schlaubi.lavakord.rest.loadItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentLinkedDeque

class GuildPlayer(
    private val link: Link,
) {
    private val mutex = Mutex()
    private val coroutine = CoroutineScope(link.coroutineContext)

    private val queue = ConcurrentLinkedDeque<Track>()
    private var currentTrack: Track? = null

    fun onTrackStart(event: TrackStartEvent) {
        currentTrack = event.track
    }

    fun onTrackEnd(event: TrackEndEvent) {
        if (!event.reason.mayStartNext) return

        coroutine.launch {
            mutex.withLock {
                playNextLocked()
            }
        }
    }

    suspend fun enqueue(track: Track) {
        mutex.withLock {
            queue.add(track)
            println("isPlaying: ${isPlaying()}")
            if (!isPlaying()) {
                playNextLocked()
            }
        }
    }

    suspend fun skip(): Track? {
        return mutex.withLock { playNextLocked() }
    }

    suspend fun stop() {
        mutex.withLock {
            currentTrack = null
            link.player.stopTrack()
        }
    }

    suspend fun removeTrackAt(position: Int): Track? {
        return mutex.withLock {
            if (position < 0 || position >= queue.size) {
                return@withLock null
            }

            queue.elementAt(position).also {
                queue.remove(it)
            }
        }
    }

    suspend fun queue(): List<Track> {
        return mutex.withLock {
            queue.toList()
        }
    }

    suspend fun search(query: String, length: Int): List<Track> {
        return when (val result = link.loadItem(query)) {
            is LoadResult.PlaylistLoaded -> result.data.tracks.take(length)
            is LoadResult.SearchResult -> result.data.tracks.take(length)
            is LoadResult.TrackLoaded -> listOf(result.data)
            else -> emptyList()
        }
    }

    private fun isPlaying(): Boolean = link.player.playingTrack != null

    private suspend fun playNextLocked(): Track? {
        println("queue: $queue")
        val next = queue.poll() ?: run {
            currentTrack = null
            return null
        }

        currentTrack = next
        println("next: $next")
        when (val result = link.loadItem(next.info.uri!!)) {
            is LoadResult.PlaylistLoaded -> {
                link.player.playTrack(result.data.tracks.first())
            }
            is LoadResult.SearchResult -> {
                link.player.playTrack(result.data.tracks.first())
            }
            is LoadResult.TrackLoaded ->  {
                link.player.playTrack(result.data)
            }

            else -> {
                println("Che dum?")
            }
        }
        return next
    }

}
