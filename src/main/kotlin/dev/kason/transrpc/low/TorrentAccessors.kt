package dev.kason.transrpc.low

import dev.kason.transrpc.Base64ToBitsetSerializer
import dev.kason.transrpc.BinaryArrayToBitsetSerializer
import dev.kason.transrpc.data.*
import dev.kason.transrpc.data.Optional
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.time.Duration

// this file implements 3.3
/** Possible fields that we can request for a torrent (section 3.3) using
 * [RpcClient.getTorrentData]. You can get the value of a given field
 * for a [TorrentAccessorData] using the [getValue] method, which unwraps
 * the optional for the property */
@Serializable
sealed class TorrentFields<T : Any>(
    internal val key: String
) {
    abstract fun getValue(torrentAccessorData: TorrentAccessorData): T?

    // tr_stat = https://github.com/transmission/transmission/blob/main/libtransmission/transmission.h#L1428
    // tr_torrent = https://github.com/transmission/transmission/blob/main/libtransmission/torrent.h#L66

    /**
     * The last time we uploaded or downloaded piece data on this torrent.
     *
     * transmission struct: tr_stat */
    data object ActivityDate : TorrentFields<Instant>("activityDate") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Instant? =
            torrentAccessorData.activityDate.value
    }

    /**
     * When the torrent was first added.
     *
     * transmission struct: tr_stat */
    data object AddedDate : TorrentFields<Instant>("addedDate") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Instant? =
            torrentAccessorData.addedDate.value
    }

    /**
     * An array of pieceCount numbers representing the number of connected peers that have each piece,
     * or -1 if we already have the piece ourselves.
     *
     * transmission struct: tr_torrentAvailability() */
    data object Availability : TorrentFields<List<TorrentAvailability>>("availability") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): List<TorrentAvailability>? =
            torrentAccessorData.availability.value
    }

    /**
     * represents the [Priority] that this torrent has. higher priority means transmission
     * places more emphasis on downloading this
     *
     * transmission struct: tr_priority_t */
    data object BandwidthPriority : TorrentFields<Priority>("bandwidthPriority") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Priority? =
            torrentAccessorData.bandwidthPriority.value
    }

    /**
     * a comment that may be added to the torrent (for example, a website link
     * or group reference)
     *
     * `Information > Comment` for a given torrent if you are in properties
     *
     * transmission struct: tr_torrent_view */
    data object Comment : TorrentFields<String>("comment") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): String? =
            torrentAccessorData.comment.value
    }

    /**
     * Byte count of all the corrupt data you've ever downloaded for
     * this torrent. If you're on a poisoned torrent, this number can
     * grow very large.
     *
     * transmission struct: tr_stat */
    data object CorruptEver : TorrentFields<ByteCount>("corruptEver") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): ByteCount? =
            torrentAccessorData.corruptEver.value
    }

    /**
     * The name of the creator of this torrent
     *
     * `Information > Origin` tells you the creator + creation date
     *
     * transmission struct: tr_torrent_view */
    data object Creator : TorrentFields<String>("creator") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): String? =
            torrentAccessorData.creator.value
    }

    /**
     * When this torrent was created
     *
     * transmission struct: tr_torrent_view */
    data object DateCreated : TorrentFields<Instant>("dateCreated") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Instant? =
            torrentAccessorData.dateCreated.value
    }

    /**
     * Byte count of all the piece data we want and don't have yet,
     * but that a connected peer does have. [0...leftUntilDone]
     *
     * transmission struct: tr_stat */
    data object DesiredAvailable : TorrentFields<ByteCount>("desiredAvailable") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): ByteCount? =
            torrentAccessorData.desiredAvailable.value
    }

    /**
     * When the torrent finished downloading.
     *
     * transmission struct: tr_stat */
    data object DoneDate : TorrentFields<Instant>("doneDate") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Instant? =
            torrentAccessorData.doneDate.value
    }

    /**
     * The directory that this torrent is downloading (or downloaded) into
     *
     * transmission struct: tr_torrent */
    data object DownloadDir : TorrentFields<String>("downloadDir") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): String? =
            torrentAccessorData.downloadDir.value
    }

    /**
     * Byte count of all the non-corrupt data you've ever downloaded
     * for this torrent. If you deleted the files and downloaded a second
     * time, this will be `2*totalSize`..
     *
     * transmission struct: tr_stat */
    data object DownloadedEver : TorrentFields<ByteCount>("downloadedEver") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): ByteCount? =
            torrentAccessorData.downloadedEver.value
    }

    /**
     * Represents the max speed that can be downloaded for this torrent (kbps)
     *
     * transmission struct: tr_torrent */
    data object DownloadLimit : TorrentFields<Speed>("downloadLimit") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Speed? =
            torrentAccessorData.downloadLimit.value
    }

    /**
     * Whether the [DownloadLimit] is honored
     *
     * transmission struct: tr_torrent */
    data object DownloadLimited : TorrentFields<Boolean>("downloadLimited") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Boolean? =
            torrentAccessorData.downloadLimited.value
    }

    /**
     * The last time during this session that a rarely-changing field
     * changed -- e.g. any `tr_torrent_metainfo` field (trackers, filenames, name)
     * or download directory. RPC clients can monitor this to know when
     * to reload fields that rarely change.
     *
     * transmission struct: tr_stat */
    data object EditDate : TorrentFields<Instant>("editDate") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Instant? =
            torrentAccessorData.editDate.value
    }

    /**
     * Defines what kind of text is in errorString.
     *
     * transmission struct: tr_stat */
    data object Error : TorrentFields<TorrentErrorType>("error") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): TorrentErrorType? =
            torrentAccessorData.error.value
    }

    /**
     * A warning or error message regarding the torrent.
     * If there is no error, this value is blank
     *
     * transmission struct: tr_stat */
    data object ErrorString : TorrentFields<String>("errorString") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): String? =
            torrentAccessorData.errorString.value
    }

    /**
     * If downloading, estimated number of seconds left until the torrent is done.
     * If seeding, estimated number of seconds left until seed ratio is reached.
     *
     * transmission struct: tr_stat */
    data object Eta : TorrentFields<Duration>("eta") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Duration? =
            torrentAccessorData.eta.value
    }

    /**
     * If seeding, number of seconds left until the idle time limit is reached.
     *
     * transmission struct: tr_stat */
    data object EtaIdle : TorrentFields<Duration>("etaIdle") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Duration? =
            torrentAccessorData.etaIdle.value
    }

    /**
     * The number of files that this torrent has.
     *
     * transmission struct: tr_info */
    data object FileCount : TorrentFields<Int>("file-count") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.fileCount.value
    }

    /** A list of objects that describe the files that are contained within this torrent.
     * Each object is of type [FileData] - the order of the file data objects within this list
     * is based on the order of the files in the torrent itself. (indexed 0 to file count)
     *
     * file data struct: tr_file_view*/
    data object Files : TorrentFields<List<FileData>>("files") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): List<FileData>? =
            torrentAccessorData.files.value
    }

    /**  A list of objects that describe the files' non-constant properties (or so the
     * transmission documentation claims). Same thing as [Files]: a list of [FileStatsData] objects
     * for each file in the torrent, in that order. (indexed 0 to file count)
     *
     * file data struct: tr_file_view (still) */
    data object FileStats : TorrentFields<List<Int>>("fileStats") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): List<Int>? =
            torrentAccessorData.fileStats.value
    }

    /** The group name of this torrent (tier that this torrent is in) */
    data object Group : TorrentFields<String>("group") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): String? =
            torrentAccessorData.group.value
    }

    /**
     * the SHA1 hash string associated with this torrent. Automatically converted
     * to a [TorrentId.ShaHash] for convenience, but the actual string itself
     * can be accessible through [TorrentId.ShaHash.hash] if needed
     *
     * transmission struct: tr_torrent_view */
    data object HashString : TorrentFields<TorrentId.ShaHash>("hashString") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): TorrentId.ShaHash? =
            torrentAccessorData.hashString.value
    }

    /**
     * Byte count of all the partial piece data we have for this torrent.
     * As pieces become complete, this value may decrease as portions of it
     * are moved to `corrupt` or `haveValid`.
     *
     * transmission struct: tr_stat */
    data object HaveUnchecked : TorrentFields<ByteCount>("haveUnchecked") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): ByteCount? =
            torrentAccessorData.haveUnchecked.value
    }

    /**
     * Byte count of all the checksum-verified data we have for this torrent.
     *
     * transmission struct: tr_stat */
    data object HaveValid : TorrentFields<ByteCount>("haveValid") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): ByteCount? =
            torrentAccessorData.haveValid.value
    }

    /**
     * Whether this torrent will honor the session limits (honors the speed limit download
     * speed limit, probably)
     *
     * transmission struct: tr_torrent
     *
     * [method that produces this value?](https://github.com/transmission/transmission/blob/main/libtransmission/transmission.h#L492)
     * */
    data object HonorsSessionLimits : TorrentFields<Boolean>("honorsSessionLimits") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Boolean? =
            torrentAccessorData.honorsSessionLimits.value
    }

    /**
     * The transmission session id for this torrent. See [TorrentId.SessionId]
     * for more details.
     *
     * transmission struct: tr_torrent */
    data object Id : TorrentFields<TorrentId.SessionId>("id") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): TorrentId.SessionId? =
            torrentAccessorData.id.value
    }

    /**
     * A torrent is considered finished if it has met its seed ratio.
     *         As a result, only paused torrents can be finished.
     *
     * transmission struct: tr_stat */
    data object IsFinished : TorrentFields<Boolean>("isFinished") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Boolean? =
            torrentAccessorData.isFinished.value
    }

    /**
     * If this torrent is from a private tracker.
     *
     * transmission struct: tr_torrent */
    data object IsPrivate : TorrentFields<Boolean>("isPrivate") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Boolean? =
            torrentAccessorData.isPrivate.value
    }

    /**
     * True if the torrent is running, but has been idle for long enough
     *         to be considered stalled.
     *
     * transmission struct: tr_stat */
    data object IsStalled : TorrentFields<Boolean>("isStalled") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Boolean? =
            torrentAccessorData.isStalled.value
    }

    /**
     * A list of strings that describe the labels assigned to this torrent
     *
     * transmission struct: tr_torrent */
    data object Labels : TorrentFields<List<String>>("labels") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): List<String>? =
            torrentAccessorData.labels.value
    }

    /**
     *Byte count of how much data is left to be downloaded until we've got
     *         all the pieces that we want. [0...tr_stat.sizeWhenDone]
     *
     * transmission struct: tr_stat */
    data object LeftUntilDone : TorrentFields<ByteCount>("leftUntilDone") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): ByteCount? =
            torrentAccessorData.leftUntilDone.value
    }

    /**
     * The magnet link for this torrent (starts with magnet:?...)
     * This exists even for torrents where you used a .torrent file,
     * same as `Copy Magnet Link to Clipboard` option in transmission gtk
     * */
    data object MagnetLink : TorrentFields<String>("magnetLink") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): String? =
            torrentAccessorData.magnetLink.value
    }

    /**
     * Last time you manually announced this torrent, or -1 if you have never done so.
     * (seems to be always -1 in testing though)
     *
     * transmission struct: tr_stat */
    data object ManualAnnounceTime : TorrentFields<Int>("manualAnnounceTime") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.manualAnnounceTime.value
    }

    /**
     * The maximum number of peers this torrent can receive. Can be seen
     * in `Properties > Options`. Identical to [PeerLimit]
     *
     * transmission struct: tr_torrent */
    @Deprecated("use peer limit instead", ReplaceWith("PeerLimit"))
    data object MaxConnectedPeers : TorrentFields<Int>("maxConnectedPeers") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.maxConnectedPeers.value
    }

    /**
     * How much of the metadata the torrent has.
     * For torrents added from a torrent this will always be 1.
     * For magnet links, this number will range from 0 to 1 as the metadata is downloaded.
     * Range is [0..1]
     * transmission struct: tr_stat */
    data object MetadataPercentComplete : TorrentFields<Double>("metadataPercentComplete") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Double? =
            torrentAccessorData.metadataPercentComplete.value
    }

    /**
     * The string name of this torrent; for example
     * `linuxmint-22.1-cinnamon-64bit.iso`. Tends to be similar to the name
     * of the torrent file you added (though not always); it's the name of
     * the torrent in the transmission gui.
     *
     * transmission struct: tr_torrent_view */
    data object Name : TorrentFields<String>("name") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): String? =
            torrentAccessorData.name.value
    }

    /**
     * The maximum number of peers this torrent can receive. Can be seen
     *      * in `Properties > Options`
     *
     * transmission struct: tr_torrent */
    data object PeerLimit : TorrentFields<Int>("peer-limit") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.peerLimit.value
    }

    /** contains [PeerData] objects that describe each peer;
     * data from `tr_peer_stat` */
    data object Peers : TorrentFields<List<PeerData>>("peers") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): List<PeerData>? =
            torrentAccessorData.peers.value
    }

    /**
     * Number of peers that we're connected to
     *
     * transmission struct: tr_stat */
    data object PeersConnected : TorrentFields<Int>("peersConnected") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.peersConnected.value
    }

    /** A [PeersFromData] object that describes how many peers we
     * got from various sources */
    data object PeersFrom : TorrentFields<PeersFromData>("peersFrom") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): PeersFromData? =
            torrentAccessorData.peersFrom.value
    }

    /**
     * Number of peers that we're sending data to
     *
     * transmission struct: tr_stat */
    data object PeersGettingFromUs : TorrentFields<Int>("peersGettingFromUs") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.peersGettingFromUs.value
    }

    /**
     * Number of peers that are sending data to us.
     *
     * transmission struct: tr_stat */
    data object PeersSendingToUs : TorrentFields<Int>("peersSendingToUs") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.peersSendingToUs.value
    }

    /**
     * How much has been downloaded of the entire torrent.
     *
     * Range is [0..1]
     *
     * (Note that if we do not want the entire file this will never reach 1 -
     * if you want a percentage for the progress out of the files that the user wants,
     * see [PercentDone])
     *
     * transmission struct: tr_stat */
    data object PercentComplete : TorrentFields<Double>("percentComplete") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Double? =
            torrentAccessorData.percentComplete.value
    }

    /**
     * How much has been downloaded of the files the user wants. This differs
     * from `percentComplete` if the user wants only some of the torrent's files.
     *
     * Range is [0..1]
     *
     * transmission struct: tr_stat */
    data object PercentDone : TorrentFields<Double>("percentDone") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Double? =
            torrentAccessorData.percentDone.value
    }

    /**
     * A bitset that represents whether we have a given piece. For example
     * ```
     * val bitset = rpc.getTorrentData(..., listOf(Pieces))[0].pieces.value!!
     * bitset[0] // <-- if we have the 1st piece
     * bitset[1] // <-- if we have the 2nd piece
     * bitset[n] // <-- if we have the n+1th piece (0 indexed)
     * ```
     *
     * Note that a bitset IS mutable; it would be best to not mutate the bitset
     * after so that the data can be used for longer, but doing won't impact the library.
     * Also, the bitset is not continuously updated; if you want that check out the
     * higher-level API (coming soon)
     *
     * transmission struct: tr_torrent */
    data object Pieces : TorrentFields<BitSet>("pieces") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): BitSet? =
            torrentAccessorData.pieces.value
    }

    /**
     * The number of pieces in this torrent.
     *
     * [Piece def](https://en.wikipedia.org/wiki/Glossary_of_BitTorrent_terms#Piece)
     *
     * transmission struct: tr_torrent_view */
    data object PieceCount : TorrentFields<Int>("pieceCount") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.pieceCount.value
    }

    /**
     * The sizes of pieces in this torrent. There are also [PieceSize] pieces.
     *
     * [Piece def](https://en.wikipedia.org/wiki/Glossary_of_BitTorrent_terms#Piece)
     *
     * transmission struct: tr_torrent_view */
    data object PieceSize : TorrentFields<Int>("pieceSize") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.pieceSize.value
    }

    /** An array of [FileCount] [Priority]s that indicate the priority given
     * to that file in the torrent */
    data object Priorities : TorrentFields<List<Priority>>("priorities") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): List<Priority>? =
            torrentAccessorData.priorities.value
    }

    /**
     * Return the mime-type (e.g. "audio/x-flac") that matches more of the
     *         torrent's content than any other mime-type
     *
     * transmission struct: tr_torrent */
    data object PrimaryMimeType : TorrentFields<String>("primary-mime-type") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): String? =
            torrentAccessorData.primaryMimeType.value
    }

    /**
     * This torrent's queue position.
     * All torrents have a queue position, even if it's not queued.
     *
     * Lowest queue position is 0, not 1
     *
     * transmission struct: tr_stat */
    data object QueuePosition : TorrentFields<Int>("queuePosition") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.queuePosition.value
    }

    /**
     * Speed all piece being received for this torrent.
     * This ONLY counts piece data.
     *
     * transmission struct: tr_stat (probably float pieceDownloadSpeed_KBps) */
    data object RateDownload : TorrentFields<Speed>("rateDownload") {
        // funny since they store these as floats in kbps, but multiply them by
        // 1000 to get the number of bytes per second to send to us...
        // why not just keep as kbps and send us that :kagathink:
        override fun getValue(torrentAccessorData: TorrentAccessorData): Speed? =
            torrentAccessorData.rateDownload.value
    }

    /**
     * Speed all piece being sent for this torrent.
     * This ONLY counts piece data.
     *
     * transmission struct: tr_stat (probably float pieceUploadSpeed_KBps) */
    data object RateUpload : TorrentFields<Speed>("rateUpload") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Speed? =
            torrentAccessorData.rateUpload.value
    }

    /**
     * When [TorrentFields.Status] is [TorrentStatus.QueuedVerify] or
     * [TorrentStatus.QueuedVerify],
     *         this is the percentage of how much of the files has been
     *         verified. When it gets to 1, the verify process is done.
     *         Range is [0..1]
     *
     * transmission struct: tr_stat */
    data object RecheckProgress : TorrentFields<Double>("recheckProgress") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Double? =
            torrentAccessorData.recheckProgress.value
    }

    /**
     * Cumulative duration the torrent's ever spent downloading
     * (second precision)
     *
     * transmission struct: tr_stat */
    data object SecondsDownloading : TorrentFields<Duration>("secondsDownloading") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Duration? =
            torrentAccessorData.secondsDownloading.value
    }

    /**
     * Cumulative duration the torrent's ever spent seeding (second precision)
     *
     * transmission struct: tr_stat */
    data object SecondsSeeding : TorrentFields<Duration>("secondsSeeding") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Duration? =
            torrentAccessorData.secondsSeeding.value
    }

    /**
     * The number of minutes of being idle before the torrent stops
     * attempting to seed. This setting is only active if the [SeedIdleMode] is
     * set to [TorrentIdleMode.Single]
     *
     * transmission struct: tr_torrent */
    data object SeedIdleLimit : TorrentFields<Int>("seedIdleLimit") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.seedIdleLimit.value
    }

    /**
     * The [TorrentIdleMode] for this torrent.
     *
     * transmission struct: tr_inactivelimit */
    data object SeedIdleMode : TorrentFields<TorrentIdleMode>("seedIdleMode") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): TorrentIdleMode? =
            torrentAccessorData.seedIdleMode.value
    }

    /**
     * Torrent level seeding ratio. This is only applied if [SeedRatioMode] is
     * [TorrentRatioLimit.Single]
     *
     * transmission struct: tr_torrent */
    data object SeedRatioLimit : TorrentFields<Double>("seedRatioLimit") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Double? =
            torrentAccessorData.seedRatioLimit.value
    }

    /**
     * The [TorrentRatioLimit] for this torrent.
     *
     * transmission struct: tr_ratiolimit */
    data object SeedRatioMode : TorrentFields<TorrentRatioLimit>("seedRatioMode") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): TorrentRatioLimit? =
            torrentAccessorData.seedRatioMode.value
    }

    /**
     * A boolean describing whether pieces for this torrent must be downloaded
     * sequentially (aka starting from the start towards the end without jumps)
     *
     * transmission struct: tr_torrent */
    data object SequentialDownload : TorrentFields<Boolean>("sequential_download") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Boolean? =
            torrentAccessorData.sequentialDownload.value
    }

    /**
     * Byte count of all the piece data we'll have downloaded when we're done,
     * whether we have it yet. If we only want some of the files,
     * this may be less than [TotalSize].
     * [0...tr_torrent_view.total_size]
     *
     * transmission struct: tr_stat */
    data object SizeWhenDone : TorrentFields<ByteCount>("sizeWhenDone") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): ByteCount? =
            torrentAccessorData.sizeWhenDone.value
    }

    /**
     * When the torrent was last started.
     *
     * transmission struct: tr_stat */
    data object StartDate : TorrentFields<Instant>("startDate") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Instant? =
            torrentAccessorData.startDate.value
    }

    /**
     * A [TorrentStatus] object describing the current state of the torrent.
     *
     * transmission struct: tr_stat */
    data object Status : TorrentFields<TorrentStatus>("status") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): TorrentStatus? =
            torrentAccessorData.status.value
    }

    /**
     * A list of [TrackerData] describing the trackers for this torrent. These
     * properties are the same as if you took them from [TrackerStats].
     *  */
    data object Trackers : TorrentFields<List<TrackerData>>("trackers") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): List<TrackerData>? =
            torrentAccessorData.trackers.value
    }

    /**
     * string of announce URLs, one per line, with a blank line between tiers */
    data object TrackerList : TorrentFields<String>("trackerList") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): String? =
            torrentAccessorData.trackerList.value
    }

    /** List of [TrackerStatsData] that describes the trackers as well. Not really sure
     * why transmission loves splitting up their structs into 2 different objects...; */
    data object TrackerStats : TorrentFields<List<TrackerStatsData>>("trackerStats") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): List<TrackerStatsData>? =
            torrentAccessorData.trackerStats.value
    }

    /**
     * The total size of the torrent, including the files that we don't want. If we
     * want to get the size of the torrent for only the files that we want, check out
     * [SizeWhenDone]
     *
     * transmission struct: tr_torrent_view */
    data object TotalSize : TorrentFields<ByteCount>("totalSize") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): ByteCount? =
            torrentAccessorData.totalSize.value
    }

    /**
     * A link to a copy of the actual .torrent file (on Linux, this goes to
     * ~/.config/transmission/torrents/{hash}.torrent)
     *
     * transmission struct: tr_info */
    data object TorrentFile : TorrentFields<String>("torrentFile") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): String? =
            torrentAccessorData.torrentFile.value
    }

    /**
     * Byte count of all data you've ever uploaded for this torrent.
     *
     * transmission struct: tr_stat */
    data object UploadedEver : TorrentFields<ByteCount>("uploadedEver") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): ByteCount? =
            torrentAccessorData.uploadedEver.value
    }

    /**
     * A [Speed] object describing the maximum upload speed for seeding this torrent.
     *
     * transmission struct: tr_torrent */
    data object UploadLimit : TorrentFields<Speed>("uploadLimit") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Speed? =
            torrentAccessorData.uploadLimit.value
    }

    /**
     * Whether the [UploadLimit] is honored.
     *
     * transmission struct: tr_torrent */
    data object UploadLimited : TorrentFields<Boolean>("uploadLimited") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Boolean? =
            torrentAccessorData.uploadLimited.value
    }

    /**
     * Total uploaded bytes / sizeWhenDone.
     * NB: In Transmission 3.00 and earlier, this was total upload / download,
     * which caused edge cases when total download was less than sizeWhenDone.
     *
     * transmission struct: tr_stat */
    data object UploadRatio : TorrentFields<Double>("uploadRatio") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Double? =
            torrentAccessorData.uploadRatio.value
    }

    /** a [BitSet] that describes whether we want a give file. For file with index N,
     * `bitset.get(N)` is `true` if we want it, and `false` if we don't */
    data object Wanted : TorrentFields<BitSet>("wanted") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): BitSet? =
            torrentAccessorData.wanted.value
    }

    /**
     * A list of strings that describe which webseeds that this torrent is connected to.
     *
     * [what's a web seed?](https://askubuntu.com/a/313112)
     *
     * transmission struct: tr_tracker_view */
    data object Webseeds : TorrentFields<List<String>>("webseeds") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): List<String>? =
            torrentAccessorData.webseeds.value
    }

    /**
     * Number of webseeds that are sending data to us
     *
     * transmission struct: tr_stat */
    data object WebseedsSendingToUs : TorrentFields<Int>("webseedsSendingToUs") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.webseedsSendingToUs.value
    }
}

// table parsing sounds very difficult for kotlinx serialization
// so even though json parsing is less efficient, we'll have to do that

/** A data object that represents the results of an access request, containing
 * all the possible fields on a torrent. All the properties
 * here are [Optional]s; if the given property was requested, then the value
 * will be [Optional.Some] with the given value, but if not it will be [Optional.None].
 *
 * see [RpcClient.getTorrentData] for more information. Documentation for what each field
 * does is in [TorrentFields]. */
@Serializable
data class TorrentAccessorData(
    /** property corresponding to [TorrentFields.ActivityDate] */
    val activityDate: Optional<@Serializable(with = UnixTimeSerializer::class) Instant> = Optional.None(),
    /** property corresponding to [TorrentFields.AddedDate] */
    val addedDate: Optional<@Serializable(with = UnixTimeSerializer::class) Instant> = Optional.None(),
    /** property corresponding to [TorrentFields.Availability] */
    val availability: Optional<List<TorrentAvailability>> = Optional.None(),
    /** property corresponding to [TorrentFields.BandwidthPriority] */
    val bandwidthPriority: Optional<Priority> = Optional.None(),
    /** property corresponding to [TorrentFields.Comment] */
    val comment: Optional<String> = Optional.None(),
    /** property corresponding to [TorrentFields.CorruptEver] */
    val corruptEver: Optional<ByteCount> = Optional.None(),
    /** property corresponding to [TorrentFields.Creator] */
    val creator: Optional<String> = Optional.None(),
    /** property corresponding to [TorrentFields.DateCreated] */
    val dateCreated: Optional<@Serializable(with = UnixTimeSerializer::class) Instant> = Optional.None(),
    /** property corresponding to [TorrentFields.DesiredAvailable] */
    val desiredAvailable: Optional<ByteCount> = Optional.None(),
    /** property corresponding to [TorrentFields.DoneDate] */
    val doneDate: Optional<@Serializable(with = UnixTimeSerializer::class) Instant> = Optional.None(),
    /** property corresponding to [TorrentFields.DownloadDir] */
    val downloadDir: Optional<String> = Optional.None(),
    /** property corresponding to [TorrentFields.DownloadedEver] */
    val downloadedEver: Optional<ByteCount> = Optional.None(),
    /** property corresponding to [TorrentFields.DownloadLimit] */
    val downloadLimit: Optional<Speed> = Optional.None(),
    /** property corresponding to [TorrentFields.DownloadLimited] */
    val downloadLimited: Optional<Boolean> = Optional.None(),
    /** property corresponding to [TorrentFields.EditDate] */
    val editDate: Optional<@Serializable(with = UnixTimeSerializer::class) Instant> = Optional.None(),
    /** property corresponding to [TorrentFields.Error] */
    val error: Optional<TorrentErrorType> = Optional.None(),
    /** property corresponding to [TorrentFields.ErrorString] */
    val errorString: Optional<String> = Optional.None(),
    /** property corresponding to [TorrentFields.Eta] */
    val eta: Optional<@Serializable(with = DurationSerializer::class) Duration> = Optional.None(),
    /** property corresponding to [TorrentFields.EtaIdle] */
    val etaIdle: Optional<@Serializable(with = DurationSerializer::class) Duration> = Optional.None(),
    /** property corresponding to [TorrentFields.FileCount] */
    @SerialName("file-count")
    val fileCount: Optional<Int> = Optional.None(),
    /** property corresponding to [TorrentFields.Files] */
    val files: Optional<List<FileData>> = Optional.None(),
    /** property corresponding to [TorrentFields.FileStats] */
    val fileStats: Optional<List<Int>> = Optional.None(),
    /** property corresponding to [TorrentFields.Group] */
    val group: Optional<String> = Optional.None(),
    /** property corresponding to [TorrentFields.HashString] */
    val hashString: Optional<TorrentId.ShaHash> = Optional.None(),
    /** property corresponding to [TorrentFields.HaveUnchecked] */
    val haveUnchecked: Optional<ByteCount> = Optional.None(),
    /** property corresponding to [TorrentFields.HaveValid] */
    val haveValid: Optional<ByteCount> = Optional.None(),
    /** property corresponding to [TorrentFields.HonorsSessionLimits] */
    val honorsSessionLimits: Optional<Boolean> = Optional.None(),
    /** property corresponding to [TorrentFields.Id] */
    val id: Optional<TorrentId.SessionId> = Optional.None(),
    /** property corresponding to [TorrentFields.IsFinished] */
    val isFinished: Optional<Boolean> = Optional.None(),
    /** property corresponding to [TorrentFields.IsPrivate] */
    val isPrivate: Optional<Boolean> = Optional.None(),
    /** property corresponding to [TorrentFields.IsStalled] */
    val isStalled: Optional<Boolean> = Optional.None(),
    /** property corresponding to [TorrentFields.Labels] */
    val labels: Optional<List<String>> = Optional.None(),
    /** property corresponding to [TorrentFields.LeftUntilDone] */
    val leftUntilDone: Optional<ByteCount> = Optional.None(),
    /** property corresponding to [TorrentFields.MagnetLink] */
    val magnetLink: Optional<String> = Optional.None(),
    /** property corresponding to [TorrentFields.ManualAnnounceTime] */
    val manualAnnounceTime: Optional<Int> = Optional.None(),
    /** property corresponding to [TorrentFields.MaxConnectedPeers] */
    val maxConnectedPeers: Optional<Int> = Optional.None(),
    /** property corresponding to [TorrentFields.MetadataPercentComplete] */
    val metadataPercentComplete: Optional<Double> = Optional.None(),
    /** property corresponding to [TorrentFields.Name] */
    val name: Optional<String> = Optional.None(),
    /** property corresponding to [TorrentFields.PeerLimit] */
    @SerialName("peer-limit")
    val peerLimit: Optional<Int> = Optional.None(),
    /** property corresponding to [TorrentFields.Peers] */
    val peers: Optional<List<PeerData>> = Optional.None(),
    /** property corresponding to [TorrentFields.PeersConnected] */
    val peersConnected: Optional<Int> = Optional.None(),
    /** property corresponding to [TorrentFields.PeersFrom] */
    val peersFrom: Optional<PeersFromData> = Optional.None(),
    /** property corresponding to [TorrentFields.PeersGettingFromUs] */
    val peersGettingFromUs: Optional<Int> = Optional.None(),
    /** property corresponding to [TorrentFields.PeersSendingToUs] */
    val peersSendingToUs: Optional<Int> = Optional.None(),
    /** property corresponding to [TorrentFields.PercentComplete] */
    val percentComplete: Optional<Double> = Optional.None(),
    /** property corresponding to [TorrentFields.PercentDone] */
    val percentDone: Optional<Double> = Optional.None(),
    /** property corresponding to [TorrentFields.Pieces] */
    val pieces: Optional<@Serializable(with = Base64ToBitsetSerializer::class) BitSet> = Optional.None(),
    /** property corresponding to [TorrentFields.PieceCount] */
    val pieceCount: Optional<Int> = Optional.None(),
    /** property corresponding to [TorrentFields.PieceSize] */
    val pieceSize: Optional<Int> = Optional.None(),
    /** property corresponding to [TorrentFields.Priorities] */
    val priorities: Optional<List<Priority>> = Optional.None(),
    /** property corresponding to [TorrentFields.PrimaryMimeType] */
    @SerialName("primary-mime-type")
    val primaryMimeType: Optional<String> = Optional.None(),
    /** property corresponding to [TorrentFields.QueuePosition] */
    val queuePosition: Optional<Int> = Optional.None(),
    /** property corresponding to [TorrentFields.RateDownload] */
    val rateDownload: Optional<@Serializable(with = Speed.ByteSerializer::class) Speed> = Optional.None(),
    /** property corresponding to [TorrentFields.RateUpload] */
    val rateUpload: Optional<@Serializable(with = Speed.ByteSerializer::class) Speed> = Optional.None(),
    /** property corresponding to [TorrentFields.RecheckProgress] */
    val recheckProgress: Optional<Double> = Optional.None(),
    /** property corresponding to [TorrentFields.SecondsDownloading] */
    val secondsDownloading: Optional<@Serializable(with = DurationSerializer::class) Duration> = Optional.None(),
    /** property corresponding to [TorrentFields.SecondsSeeding] */
    val secondsSeeding: Optional<@Serializable(with = DurationSerializer::class) Duration> = Optional.None(),
    /** property corresponding to [TorrentFields.SeedIdleLimit] */
    val seedIdleLimit: Optional<Int> = Optional.None(),
    /** property corresponding to [TorrentFields.SeedIdleMode] */
    val seedIdleMode: Optional<TorrentIdleMode> = Optional.None(),
    /** property corresponding to [TorrentFields.SeedRatioLimit] */
    val seedRatioLimit: Optional<Double> = Optional.None(),
    /** property corresponding to [TorrentFields.SeedRatioMode] */
    val seedRatioMode: Optional<TorrentRatioLimit> = Optional.None(),
    /** property corresponding to [TorrentFields.SequentialDownload] */
    @SerialName("sequential_download")
    val sequentialDownload: Optional<Boolean> = Optional.None(),
    /** property corresponding to [TorrentFields.SizeWhenDone] */
    val sizeWhenDone: Optional<ByteCount> = Optional.None(),
    /** property corresponding to [TorrentFields.StartDate] */
    val startDate: Optional<@Serializable(with = UnixTimeSerializer::class) Instant> = Optional.None(),
    /** property corresponding to [TorrentFields.Status] */
    val status: Optional<TorrentStatus> = Optional.None(),
    /** property corresponding to [TorrentFields.Trackers] */
    val trackers: Optional<List<TrackerData>> = Optional.None(),
    /** property corresponding to [TorrentFields.TrackerList] */
    val trackerList: Optional<String> = Optional.None(),
    /** property corresponding to [TorrentFields.TrackerStats] */
    val trackerStats: Optional<List<TrackerStatsData>> = Optional.None(),
    /** property corresponding to [TorrentFields.TotalSize] */
    val totalSize: Optional<ByteCount> = Optional.None(),
    /** property corresponding to [TorrentFields.TorrentFile] */
    val torrentFile: Optional<String> = Optional.None(),
    /** property corresponding to [TorrentFields.UploadedEver] */
    val uploadedEver: Optional<ByteCount> = Optional.None(),
    /** property corresponding to [TorrentFields.UploadLimit] */
    val uploadLimit: Optional<Speed> = Optional.None(),
    /** property corresponding to [TorrentFields.UploadLimited] */
    val uploadLimited: Optional<Boolean> = Optional.None(),
    /** property corresponding to [TorrentFields.UploadRatio] */
    val uploadRatio: Optional<Double> = Optional.None(),
    /** property corresponding to [TorrentFields.Wanted] */
    val wanted: Optional<@Serializable(with = BinaryArrayToBitsetSerializer::class) BitSet> = Optional.None(),
    /** property corresponding to [TorrentFields.Webseeds] */
    val webseeds: Optional<List<String>> = Optional.None(),
    /** property corresponding to [TorrentFields.WebseedsSendingToUs] */
    val webseedsSendingToUs: Optional<Int> = Optional.None(),
) {
    /** A debug toString() method that outputs only the properties that are not set
     * to None */
    fun debugString(): String = buildString {
        append("{")
        TorrentFields::class.sealedSubclasses.forEach {
            val x = it.objectInstance!!.getValue(this@TorrentAccessorData)
            if (x != null) {
                append(it.simpleName!!.replaceFirstChar(Char::lowercase))
                    .append(":")
                    .append(x)
                    .append(",")
            }
        }
        deleteCharAt(length - 1)
        append("}")
    }

}

@Serializable
internal data class TorrentAccessorRequest(
    val ids: TorrentIds = TorrentIds.All,
    val fields: List<String>
) : RpcRequest<TorrentAccessorResponse>() {
    override val method: String
        get() = "torrent-get"
}

@Serializable
internal class TorrentAccessorResponse(val torrents: List<TorrentAccessorData>) :
    List<TorrentAccessorData> by torrents, RpcResponse

/**
 * Fetches data from the transmission daemon about the given torrents.
 *
 * Returns a list of [TorrentAccessorData] for each torrent specified by [ids] in the order
 * specified, with the fields specified by [fields] set ([TorrentFields])
 *
 * Example:
 * ```
 *   // returns TorrentAccessorData with the hash string property filled out
 *     val data = client.getTorrentData(TorrentIds.All, listOf(TorrentFields.HashString))
 *     // data is a List<TorrentAccessorData> with only the TorrentAccessorData.hashString populated
 *     // if we ended the statement here
 *          .map { it.hashString.value!! }
 *          // hashString is of type Optional<ShaHash>, so we can map to a List<ShaHash> this way
 * ```
 *
 * Beware that all the properties for [TorrentAccessorData] are optionals, with [Optional.None] indicating
 * that the given property wasn't requested in fields.
 * */
suspend fun RpcClient.getTorrentData(ids: TorrentIds, fields: List<TorrentFields<*>>): List<TorrentAccessorData> {
    return request(TorrentAccessorRequest(ids, fields.map { it.key }.toSet().toList())).torrents
}

/** helper function for [getTorrentData], but for vararg field inputs instead
 * of a list. */
suspend fun RpcClient.getTorrentData(ids: TorrentIds, vararg fields: TorrentFields<*>): List<TorrentAccessorData> =
    getTorrentData(ids, fields.toList())