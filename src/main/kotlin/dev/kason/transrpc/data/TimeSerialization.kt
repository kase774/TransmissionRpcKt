package dev.kason.transrpc.data

import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/** time_t as long serialize to instant */
object UnixTimeSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("TransmissionRpc.UnixTimeInstant", PrimitiveKind.LONG)

    override fun deserialize(decoder: Decoder): Instant =
        Instant.fromEpochSeconds(decoder.decodeLong())

    override fun serialize(encoder: Encoder, value: Instant) =
        encoder.encodeLong(value.epochSeconds)

}

/** time_t as long serialize to duration */
object DurationSerializer : KSerializer<Duration> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("TransmissionRpc.Duration", PrimitiveKind.LONG)

    override fun deserialize(decoder: Decoder): Duration =
        decoder.decodeLong().seconds

    override fun serialize(encoder: Encoder, value: Duration) =
        encoder.encodeLong(value.inWholeSeconds)

}