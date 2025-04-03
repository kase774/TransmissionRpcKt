package dev.kason.transrpc.low

import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.format.TextStyle
import java.util.*

/** Represents a schedule that is active for certain days of the week. You can query
 * whether a day of week is active in the schedule with [TrSpeedSchedule.get] */
@Serializable
@JvmInline
value class TrSpeedSchedule(val value: Int) {
    operator fun get(dayOfWeek: DayOfWeek): Boolean {
        val index = dayOfWeek.ordinal % 7 // sunday = 0
        return value and (1 shl index) != 0
    }

    fun toMap(): Map<DayOfWeek, Boolean> = buildMap {
        for (dayOfWeek in java.time.DayOfWeek.entries) {
            put(dayOfWeek, this@TrSpeedSchedule[dayOfWeek])
        }
    }

    override fun toString(): String =
        DayOfWeek.entries.joinToString { it.getDisplayName(TextStyle.NARROW, Locale.getDefault()) }
}

/** Options for the session wide encryption */
@Serializable
enum class SessionEncryption {
    @SerialName("required")
    Required,

    @SerialName("preferred")
    Preferred,

    @SerialName("tolerated")
    Tolerated
}