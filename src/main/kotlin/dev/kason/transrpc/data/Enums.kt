package dev.kason.transrpc.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/** represents a priority that can be assigned to a torrent (tr_priority_t). higher priority
 * means that transmission will place more emphasis on downloading this file / torrent*/
// https://github.com/transmission/transmission/blob/main/libtransmission/transmission.h#L75
@Serializable(with = Priority.Serializer::class)
enum class Priority(val id: Int) {
    Low(-1),
    Normal(0),
    High(1);

    object Serializer : KSerializer<Priority> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("TransmissionRpc.TorrentPriority", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): Priority =
            when (decoder.decodeInt()) {
                -1 -> Low
                0 -> Normal
                1 -> High
                else -> error("Unknown priority")
            }

        override fun serialize(encoder: Encoder, value: Priority) =
            encoder.encodeInt(value.id)
    }
}

/** idle limit for a torrent (tr_idlelimit). This defines how long it takes
 * transmission to stop trying to seed a torrent if its idle.
 *
 * There are 3 values
 *  - [Global]: we follow the session limits
 *  - [Single]: we describe # of mins of idle before we stop seeding (via
 *  [dev.kason.transrpc.low.TorrentFields.SeedIdleLimit] property
 *  - [Unlimited]: always seed
 * */
// https://github.com/transmission/transmission/blob/main/libtransmission/transmission.h#L996
@Serializable(with = TorrentIdleMode.Serializer::class)
enum class TorrentIdleMode(val id: Int) {
    // wait we don't really actually need id since its just ordinal
    // doesn't really matter though
    /** follow the global settings */
    Global(0),

    /** override the global settings, seeding until the torrent
     * has been idle for a certain number of minutes */
    Single(1),

    /** override the global settings, seeding regardless of activity */
    Unlimited(2);

    object Serializer : KSerializer<TorrentIdleMode> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("TransmissionRpc.TorrentIdleLimit", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): TorrentIdleMode =
            when (decoder.decodeInt()) {
                0 -> Global
                1 -> Single
                2 -> Unlimited
                else -> error("Unknown priority")
            }

        override fun serialize(encoder: Encoder, value: TorrentIdleMode) =
            encoder.encodeInt(value.id)
    }
}


/** ratio limit for a torrent (tr_ratiolimit) */
// https://github.com/transmission/transmission/blob/main/libtransmission/transmission.h#L976
@Serializable(with = TorrentRatioLimit.Serializer::class)
enum class TorrentRatioLimit(val id: Int) {
    /** follow the global settings */
    Global(0),

    /** override the global settings, seeding until a certain ratio */
    Single(1),

    /** override the global settings, seeding regardless of ratio */
    Unlimited(2);

    object Serializer : KSerializer<TorrentRatioLimit> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("TransmissionRpc.TorrentRatioLimit", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): TorrentRatioLimit =
            when (decoder.decodeInt()) {
                0 -> Global
                1 -> Single
                2 -> Unlimited
                else -> error("Unknown priority")
            }

        override fun serialize(encoder: Encoder, value: TorrentRatioLimit) =
            encoder.encodeInt(value.id)
    }
}

/** Describes the current state of the torrent. Queued{x}
 * means that the torrent is currently queued to do {X}, but is not
 * doing it; for example, [QueuedVerify] means that it's not verifying at this
 * moment but is queued to do so. When it is verifying, the torrent status will
 * also be [Verifying]. */
@Serializable(with = TorrentStatus.Serializer::class)
enum class TorrentStatus {
    /** Torrent is stopped, or doing nothing */
    Stopped,
    QueuedVerify,
    Verifying,
    QueuedDownload,
    Downloading,
    QueuedSeed,
    Seeding;

    object Serializer : KSerializer<TorrentStatus> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("TransmissionRpc.TorrentStatus", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): TorrentStatus =
            entries[decoder.decodeInt()]

        override fun serialize(encoder: Encoder, value: TorrentStatus) =
            encoder.encodeInt(value.ordinal)
    }
}

