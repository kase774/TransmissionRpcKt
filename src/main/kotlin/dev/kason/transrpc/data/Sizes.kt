@file:Suppress("MemberVisibilityCanBePrivate")

package dev.kason.transrpc.data

import kotlinx.serialization.Serializable

/** Represents a counter for the number of bytes (such as sizes of stuff, for example) */
@Serializable
@JvmInline
value class ByteCount(val bytes: Long) {

    init {
        require(bytes >= 0)
    }

    val kiloBytes: Double
        get() = bytes / 1000.0

    val megaBytes: Double
        get() = bytes / 1_000_000.0

    val gigaBytes: Double
        get() = bytes / 1_000_000_000.0

    val teraBytes: Double
        get() = bytes / 1_000_000_000_000.0

    override fun toString(): String {
        if (bytes < 1000) return "$bytes bytes"
        val format = "%,.3f"
        return when {
            bytes < 1_000_000 -> format.format(kiloBytes) + " KB"
            bytes < 1_000_000_000 -> format.format(megaBytes) + " MB"
            bytes < 1_000_000_000_000 -> format.format(gigaBytes) + " GB"
            else -> format.format(teraBytes) + " TB"
        }
    }
}

// Transmission::Values::Speed https://github.com/transmission/transmission/blob/main/libtransmission/values.h#L268
// i don't understand templates tho qwq
/** Represents the speed of a data exchange, in kilobytes per second */
@Serializable
@JvmInline
value class Speed(val kilobytesPerSecond: Int) {

    init {
        require(kilobytesPerSecond >= 0)
    }

    fun toByteCount(): ByteCount =
        ByteCount(kilobytesPerSecond * 1000L)

    override fun toString(): String =
        "$kilobytesPerSecond KB/s"
}