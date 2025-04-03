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
 * [gigaBytes], [teraBytes]
 *
* You can use extension
 * properties on [Int] and [Double] to construct byte count objects in an easy-to-read
 * manner:
 *
 * ```
 * val size = 5.gigaBytes
 * ```
 * */
@Serializable
@JvmInline
value class ByteCount(val bytes: Long): Comparable<ByteCount>  {

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

    override fun compareTo(other: ByteCount): Int =
        bytes.compareTo(other.bytes)

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

val Int.bytes: ByteCount
    get() = ByteCount(toLong())
val Int.kiloBytes: ByteCount
    get() = ByteCount(toLong() * 1000)
val Int.megaBytes: ByteCount
    get() = ByteCount(toLong() * 1_000_000)
val Int.gigaBytes: ByteCount
    get() = ByteCount(toLong() * 1_000_000_000)
val Int.teraBytes: ByteCount
    get() = ByteCount(toLong() * 1_000_000_000_000)

val Double.bytes: ByteCount
    get() = ByteCount(roundToLong())
val Double.kiloBytes: ByteCount
    get() = ByteCount((this * 1000).roundToLong())
val Double.megaBytes: ByteCount
    get() = ByteCount((this * 1_000_000).roundToLong())
val Double.gigaBytes: ByteCount
    get() = ByteCount((this * 1_000_000_000).roundToLong())
val Double.teraBytes: ByteCount
    get() = ByteCount((this * 1_000_000_000_000).roundToLong())

// Transmission::Values::Speed https://github.com/transmission/transmission/blob/main/libtransmission/values.h#L268
// i don't understand templates tho qwq
/** Represents the speed of a data exchange, in kilobytes per second. You can use extension
 * properties on [Int] and [Double] to construct speed objects in an easy to read
 * manner:
 *
 * ```
 * val maxUploadSpeed = 5.gigaBytesPerSecond
 * val maxDownloadSpeed = 5.3.gigaBytesPerSecond
 * ```
 *
 * */
@Serializable
@JvmInline
value class Speed(val kilobytesPerSecond: Double): Comparable<Speed> {

    init {
        require(kilobytesPerSecond >= 0)
    }

    fun toByteCount(): ByteCount =
        ByteCount((kilobytesPerSecond * 1000L).roundToLong())

    override fun toString(): String =
        "%,.3f".format(kilobytesPerSecond) + " KB/s"


    override fun compareTo(other: Speed): Int =
        kilobytesPerSecond.compareTo(other.kilobytesPerSecond)

    /** Serializer for use when a property is measured in terms of bytes */
    // only the kb/s are actually measured, they just multiply by 1k
    object ByteSerializer : KSerializer<Speed> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("TransmissionRpc.SpeedAsBytes", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): Speed = decoder.decodeInt().bytesPerSecond

        override fun serialize(encoder: Encoder, value: Speed) {
            encoder.encodeLong(value.toByteCount().bytes)
        }
    }

    /** Test serializer for serializing Speed as Int (still kbps) */
    object IntSerializer : KSerializer<Speed> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("TransmissionRpc.SpeedAsKbAsInt", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): Speed = decoder.decodeInt().kiloBytesPerSecond

        override fun serialize(encoder: Encoder, value: Speed) =
        encoder.encodeLong(value.kilobytesPerSecond.roundToLong())
    }
}

val Int.bytesPerSecond: Speed
    get() = Speed(this / 1000.0)
val Int.kiloBytesPerSecond: Speed
    get() = Speed(this.toDouble())
val Int.megaBytesPerSecond: Speed
    get() = Speed(this * 1000.0)
val Int.gigaBytesPerSecond: Speed
    get() = Speed(this * 1_000_000.0)
val Int.teraBytesPerSecond: Speed
    get() = Speed(this * 1_000_000_000.0)

val Double.bytesPerSecond: Speed
    get() = Speed(this / 1000.0)
val Double.kiloBytesPerSecond: Speed
    get() = Speed(this)
val Double.megaBytesPerSecond: Speed
    get() = Speed(this * 1000.0)
val Double.gigaBytesPerSecond: Speed
    get() = Speed(this * 1_000_000.0)
val Double.teraBytesPerSecond: Speed
    get() = Speed(this * 1_000_000_000.0)