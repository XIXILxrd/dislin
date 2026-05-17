@file:OptIn(KordVoice::class)

package dev.rukhlovar.models

import dev.kord.common.annotation.KordVoice
import dev.kord.voice.VoiceConnection

sealed class ConnectionResult {

    data class Connected(val voiceConnection: VoiceConnection):  ConnectionResult()

    object Error:  ConnectionResult()

    object Disconnected: ConnectionResult()
}