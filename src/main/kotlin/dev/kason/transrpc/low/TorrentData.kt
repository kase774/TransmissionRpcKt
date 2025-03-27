package dev.kason.transrpc.low

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/** each byte is set
 * to either -1 if we have the piece, otherwise it is set to the number
 * of connected peers who have the piece. */
// https://github.com/transmission/transmission/blob/main/libtransmission/transmission.h#L1371
@Serializable(with = TorrentAvailability.Serializer::class)
sealed interface TorrentAvailability {

    /** we already have this piece */
    data object HavePieceAlready : TorrentAvailability

    /** indicates that we don't already have this piece, but are getting it from [numberOfPeersConnected] peers
     * right now */
    @JvmInline
    value class Downloading(val numberOfPeersConnected: Int) : TorrentAvailability

    object Serializer : KSerializer<TorrentAvailability> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("TransmissionRpc.TorrentAvail", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): TorrentAvailability {
            val value = decoder.decodeInt()
            return if (value == -1) {
                HavePieceAlready
            } else if (value >= 0) {
                Downloading(value)
            } else {
                error("not a valid value for torrent availability: $value")
            }
        }

        override fun serialize(encoder: Encoder, value: TorrentAvailability) =
            encoder.encodeInt(if (value is Downloading) value.numberOfPeersConnected else -1)

    }
}

// error types, see https://github.com/transmission/transmission/blob/main/libtransmission/transmission.h#L1415
@Serializable(with = TorrentErrorType.Serializer::class)
enum class TorrentErrorType(val value: Int) {
    /** everything's fine */
    Ok(0),

    /** when we announced to the tracker, we got a warning in the response */
    TrackerWarning(1),

    /** when we announced to the tracker, we got an error in the response */
    TrackerError(2),

    /** local trouble, such as disk full or permissions error */
    LocalError(3);

    object Serializer : KSerializer<TorrentErrorType> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("TransmissionRpc.TorrentErrorType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): TorrentErrorType =
            when (decoder.decodeInt()) {
                0 -> Ok
                1 -> TrackerWarning
                2 -> TrackerError
                3 -> LocalError
                else -> error("Unknown priority")
            }

        override fun serialize(encoder: Encoder, value: TorrentErrorType) =
            encoder.encodeInt(value.value)
    }
}