package moe.nea.firmament.util

import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import moe.nea.firmament.Firmament
import moe.nea.firmament.events.ServerChatLineReceivedEvent
import moe.nea.firmament.events.SkyblockServerUpdateEvent
import moe.nea.firmament.events.WorldReadyEvent

object SBData {
    val profileRegex = "(?:Your profile was changed to: |You are playing on profile: )(.+)".toRegex()
    var profileCuteName: String? = null

    private var lastLocrawSent = Timer()
    private val locrawRoundtripTime: Duration = 5.seconds
    var locraw: Locraw? = null
    val skyblockLocation get() = locraw?.skyblockLocation


    fun init() {
        ServerChatLineReceivedEvent.subscribe { event ->
            val profileMatch = profileRegex.matchEntire(event.unformattedString)
            if (profileMatch != null) {
                profileCuteName = profileMatch.groupValues[1]
            }
            if (event.unformattedString.startsWith("{")) {
                if (tryReceiveLocraw(event.unformattedString) && lastLocrawSent.timePassed() < locrawRoundtripTime) {
                    lastLocrawSent.markFarPast()
                    event.cancel()
                }
            }
        }

        WorldReadyEvent.subscribe {
            sendLocraw()
            locraw = null
        }
    }

    private fun tryReceiveLocraw(unformattedString: String): Boolean = try {
        val lastLocraw = locraw
        locraw = Firmament.json.decodeFromString<Locraw>(unformattedString)
        SkyblockServerUpdateEvent.publish(SkyblockServerUpdateEvent(lastLocraw, locraw))
        true
    } catch (e: SerializationException) {
        e.printStackTrace()
        false
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        false
    }

    fun sendLocraw() {
        lastLocrawSent.markNow()
        val nh = MC.player?.networkHandler ?: return
        nh.sendChatCommand("locraw")
    }


}