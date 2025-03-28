package dev.kason.transrpc.low

import dev.kason.transrpc.data.ByteCount
import dev.kason.transrpc.data.Priority
import dev.kason.transrpc.data.Speed
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
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

/** a file object, according to 3.2 (properties from tr_file_view) */
//　https://github.com/transmission/transmission/blob/main/libtransmission/transmission.h#L1299
@Serializable
data class FileData(
    /** The number of bytes we have already */
    val bytesCompleted: ByteCount,
    /** The total size of the file */
    val length: ByteCount,
    val name: String,
    @SerialName("begin_piece")
    /** the index of the first piece that is part of this file */
    val beginPieceIndex: Int,
    @SerialName("end_piece")
    /** piece index where this file ends (exclusive) */
    val endPieceIndex: Int
)

/** fileStats: a file's non-constant properties.
 * An array of tr_info.filecount objects, in the same order as files. This class represents
 * one of those objects */
// don't really see why these should be 2 different objects, but the spec
// gives us these as so :kagathink:
@Serializable
data class FileStatsData(
    /** The number of bytes we have already */
    val bytesCompleted: ByteCount,
    val wanted: Boolean,
    val priority: Priority
)

/** Represents the data for a peer */
// https://github.com/transmission/transmission/blob/main/libtransmission/transmission.h#L1172
@Serializable
data class PeerData(
    // this is the only time we use address i think
    // so no need to make a wrapper class for this
    val address: String,
    val clientName: String,
    val clientIsChoked: Boolean,
    val clientIsInterested: Boolean,
    @SerialName("flagStr")
    val flagString: String,
    val isDownloadingFrom: Boolean,
    val isEncrypted: Boolean,
    val isIncoming: Boolean,
    val isUploadingTo: Boolean,
    val isUTP: Boolean,
    val peerIsChocked: Boolean,
    val peerIsInterested: Boolean,
    val port: Int,
    val progress: Double,
    val rateToClient: @Serializable(with = Speed.ByteSerializer::class) Speed,
    val rateToPeer: @Serializable(with = Speed.ByteSerializer::class) Speed
)


//@Serializable
//data class PeersFromData(
//
//)