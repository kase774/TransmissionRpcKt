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
    /** The number of bytes we have already (should be same as [FileData.bytesCompleted]) */
    val bytesCompleted: ByteCount,
    /** Whether we want this file or not */
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
    val peerIsChoked: Boolean,
    val peerIsInterested: Boolean,
    val port: Int,
    val progress: Double,
    val rateToClient: @Serializable(with = Speed.ByteSerializer::class) Speed,
    val rateToPeer: @Serializable(with = Speed.ByteSerializer::class) Speed
)


/** A data object that describes how many peers we got from various
 * peer sources. see individual property KDocs for more details
 *
 * [struct tr_peer_from](https://github.com/transmission/transmission/blob/main/libtransmission/transmission.h#L1398) */
@Serializable
data class PeersFromData(
    /** assume is from_resume? peers found in the .resume file */
    val fromCache: Int,
    /** peers found from the DHT  */
    val fromDht: Int,
    /** connections made to the listening port */
    val fromIncoming: Int,
    /** peers found from local announcements */
    val fromLpd: Int,
    /** peers where peer address provided in an LTEP handshake */
    val fromLtep: Int,
    /** peers found from peer exchange  */
    val fromPex: Int,
    /** peers found from a tracker */
    val fromTracker: Int
)

// link https://github.com/transmission/transmission/blob/ac5c9e082da257e102eb4ff18f2e433976a585d1/libtransmission/transmission.h#L1241
/** Represents tracker data (some more properties in [TrackerStatsData], please see asw)*/
@Serializable
data class TrackerData(
    /** full announce URL */
    val announce: String,
    /** unique transmission-generated ID for use in libtransmission API */
    val id: Int,
    /** full scrape URL */
    val scrape: String,
    /** The tracker site's name. Uses the first label before the public suffix
    (https://publicsuffix.org/) in the announce URL's host.
    e.g. "https://www.example.co.uk/announce/"'s sitename is "example"
    RFC 1034 says labels must be less than 64 chars */
    @SerialName("sitename")
    val siteName: String,
    /** which tier this tracker is in */
    val tier: Int
)

// todo finish docs for this class
@Serializable
data class TrackerStatsData(
    /** same as [TrackerData.announce] */
    val announce: String,
    /** the [TrackerState] for announcing */
    val announceState: TrackerState,
    /** number of times this torrent's been downloaded, or -1 if unknown */
    val downloadCount: Int,
    /** true iff we've announced to this tracker during this session */
    val hasAnnounced: Boolean,
    /** true iff we've scraped this tracker during this session */
    val hasScraped: Boolean,
    val host: String,
    val id: Int,
    val isBackup: Boolean,
    val lastAnnouncePeerCount: Long,
    val lastAnnounceResult: String,
    val lastAnnounceStartTime: Long,
    val lastAnnounceSucceeded: Boolean,
    val lastAnnounceTime: Long,
    val lastAnnounceTimedOut: Boolean,
    val lastScrapeResult: String,
    val lastScrapeStartTime: Long,
    val lastScrapeSucceeded: Boolean,
    val lastScrapeTime: Long,
    val lastScrapeTimedOut: Boolean,
    val leecherCount: Int,
    val nextAnnounceTime: Long,
    val nextScrapeTime: Long,
    val scrape: String,
    /** [TrackerState] for scraping */
    val scrapeState: TrackerState,
    val seederCount: Int,
    /** The tracker site's name. Uses the first label before the public suffix
    (https://publicsuffix.org/) in the announce URL's host.
    e.g. "https://www.example.co.uk/announce/"'s sitename is "example"
    RFC 1034 says labels must be less than 64 chars.

     Same as [TrackerData.siteName]*/
    @SerialName("sitename")
    val siteName: String,
    val tier: Int
) {
    /** describes the state of the tracker for that action (announcing / scraping).
     *
     * There are 4 values:
     *  - [Inactive]
     *  - [Waiting]
     *  - [Queued]
     *  - [Active]*/
    @Serializable(with = TrackerState.Serializer::class)
    enum class TrackerState {

        /** we won't (announce,scrape) this torrent to this tracker because
         * the torrent is stopped, or because of an error, or whatever */
        Inactive,

        /** we will (announce,scrape) this torrent to this tracker, and are
         * waiting for enough time to pass to satisfy the tracker's interval */
        Waiting,

        /** it's time to (announce,scrape) this torrent, and we're waiting on a
         * free slot to open up in the announce manager */
        Queued,

        /** we're (announcing,scraping) this torrent right now */
        Active;

        object Serializer : KSerializer<TrackerState> {
            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("TransmissionRpc.TrackerState", PrimitiveKind.INT)

            override fun deserialize(decoder: Decoder): TrackerState =
                entries[decoder.decodeInt()]

            override fun serialize(encoder: Encoder, value: TrackerState) =
                encoder.encodeInt(value.ordinal)

        }
    }
}