package dev.rukhlovar.models

enum class TrackSource(val title: String, val prefix: String) {
    YOUTUBE("Youtube", "ytsearch"),
    YOUTUBE_MUSIC("Youtube Music", "ytmsearch"),
    SOUND_CLOUD("Sound Cloud", "scsearch"),
}