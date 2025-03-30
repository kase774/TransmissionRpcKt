@file:Suppress("MemberVisibilityCanBePrivate")

package dev.kason.transrpc.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.math.roundToLong

/** Represents a counter for the number of bytes (such as sizes of stuff, for example)
 *
 * Helper properties for simple conversion between counters: [kiloBytes], [megaBytes],
 * [gigaBytes], [teraBytes]*/
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

@Suppress("FunctionName")
fun SpeedFromBytes(bytesPerSecond: Double) =
    Speed(bytesPerSecond / 1000)

// Transmission::Values::Speed https://github.com/transmission/transmission/blob/main/libtransmission/values.h#L268
// i don't understand templates tho qwq
/** Represents the speed of a data exchange, in kilobytes per second */
@Serializable
@JvmInline
value class Speed(val kilobytesPerSecond: Double) {

    init {
        require(kilobytesPerSecond >= 0)
    }

    fun toByteCount(): ByteCount =
        ByteCount((kilobytesPerSecond * 1000L).roundToLong())

    override fun toString(): String =
        "%,.3f".format(kilobytesPerSecond) + " KB/s"

    /** Serializer for use when a property is measured in terms of bytes */
    // only the kb/s are actually measured, they just multiply by 1k
    object ByteSerializer : KSerializer<Speed> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("TransmissionRpc.SpeedAsBytes", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): Speed = SpeedFromBytes(decoder.decodeDouble())

        override fun serialize(encoder: Encoder, value: Speed) {
            encoder.encodeDouble(value.kilobytesPerSecond * 1000)
        }

    }
}