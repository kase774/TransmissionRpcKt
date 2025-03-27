package dev.kason.transrpc.low

import dev.kason.transrpc.data.*
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration

// this file implements 3.3
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
     * represents the [Priority] that this torrent has
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

    /**  */
    data object Files : TorrentFields<List<Int>>("files") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): List<Int>? =
            torrentAccessorData.files.value
    }

    /**  */
    data object FileStats : TorrentFields<List<Int>>("fileStats") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): List<Int>? =
            torrentAccessorData.fileStats.value
    }

    /**  */
    data object Group : TorrentFields<String>("group") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): String? =
            torrentAccessorData.group.value
    }

    /**
     *
     * transmission struct: tr_torrent_view */
    data object HashString : TorrentFields<String>("hashString") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): String? =
            torrentAccessorData.hashString.value
    }

    /**
     *
     * transmission struct: tr_stat */
    data object HaveUnchecked : TorrentFields<Int>("haveUnchecked") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.haveUnchecked.value
    }

    /**
     *
     * transmission struct: tr_stat */
    data object HaveValid : TorrentFields<Int>("haveValid") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.haveValid.value
    }

    /**
     *
     * transmission struct: tr_torrent */
    data object HonorsSessionLimits : TorrentFields<Boolean>("honorsSessionLimits") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Boolean? =
            torrentAccessorData.honorsSessionLimits.value
    }

    /**
     *
     * transmission struct: tr_torrent */
    data object Id : TorrentFields<Int>("id") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.id.value
    }

    /**
     *
     * transmission struct: tr_stat */
    data object IsFinished : TorrentFields<Boolean>("isFinished") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Boolean? =
            torrentAccessorData.isFinished.value
    }

    /**
     *
     * transmission struct: tr_torrent */
    data object IsPrivate : TorrentFields<Boolean>("isPrivate") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Boolean? =
            torrentAccessorData.isPrivate.value
    }

    /**
     *
     * transmission struct: tr_stat */
    data object IsStalled : TorrentFields<Boolean>("isStalled") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Boolean? =
            torrentAccessorData.isStalled.value
    }

    /**
     *
     * transmission struct: tr_torrent */
    data object Labels : TorrentFields<List<String>>("labels") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): List<String>? =
            torrentAccessorData.labels.value
    }

    /**
     *
     * transmission struct: tr_stat */
    data object LeftUntilDone : TorrentFields<Int>("leftUntilDone") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.leftUntilDone.value
    }

    /**  */
    data object MagnetLink : TorrentFields<String>("magnetLink") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): String? =
            torrentAccessorData.magnetLink.value
    }

    /**
     *
     * transmission struct: tr_stat */
    data object ManualAnnounceTime : TorrentFields<Int>("manualAnnounceTime") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.manualAnnounceTime.value
    }

    /**
     *
     * transmission struct: tr_torrent */
    data object MaxConnectedPeers : TorrentFields<Int>("maxConnectedPeers") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.maxConnectedPeers.value
    }

    /**
     *
     * transmission struct: tr_stat */
    data object MetadataPercentComplete : TorrentFields<Double>("metadataPercentComplete") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Double? =
            torrentAccessorData.metadataPercentComplete.value
    }

    /**
     *
     * transmission struct: tr_torrent_view */
    data object Name : TorrentFields<String>("name") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): String? =
            torrentAccessorData.name.value
    }

    /**
     *
     * transmission struct: tr_torrent */
    data object PeerLimit : TorrentFields<Int>("peer-limit") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.peerLimit.value
    }

    /**  */
    data object Peers : TorrentFields<List<Int>>("peers") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): List<Int>? =
            torrentAccessorData.peers.value
    }

    /**
     *
     * transmission struct: tr_stat */
    data object PeersConnected : TorrentFields<Int>("peersConnected") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.peersConnected.value
    }

    /**  */
    data object PeersFrom : TorrentFields<List<String>>("peersFrom") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): List<String>? =
            torrentAccessorData.peersFrom.value
    }

    /**
     *
     * transmission struct: tr_stat */
    data object PeersGettingFromUs : TorrentFields<Int>("peersGettingFromUs") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.peersGettingFromUs.value
    }

    /**
     *
     * transmission struct: tr_stat */
    data object PeersSendingToUs : TorrentFields<Int>("peersSendingToUs") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.peersSendingToUs.value
    }

    /**
     *
     * transmission struct: tr_stat */
    data object PercentComplete : TorrentFields<Double>("percentComplete") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Double? =
            torrentAccessorData.percentComplete.value
    }

    /**
     *
     * transmission struct: tr_stat */
    data object PercentDone : TorrentFields<Double>("percentDone") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Double? =
            torrentAccessorData.percentDone.value
    }

    /**
     *
     * transmission struct: tr_torrent */
    data object Pieces : TorrentFields<String>("pieces") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): String? =
            torrentAccessorData.pieces.value
    }

    /**
     *
     * transmission struct: tr_torrent_view */
    data object PieceCount : TorrentFields<Int>("pieceCount") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.pieceCount.value
    }

    /**
     *
     * transmission struct: tr_torrent_view */
    data object PieceSize : TorrentFields<Int>("pieceSize") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.pieceSize.value
    }

    /**  */
    data object Priorities : TorrentFields<List<Int>>("priorities") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): List<Int>? =
            torrentAccessorData.priorities.value
    }

    /**
     *
     * transmission struct: tr_torrent */
    data object PrimaryMimeType : TorrentFields<String>("primary-mime-type") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): String? =
            torrentAccessorData.primaryMimeType.value
    }

    /**
     *
     * transmission struct: tr_stat */
    data object QueuePosition : TorrentFields<Int>("queuePosition") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.queuePosition.value
    }

    /**
     *
     * transmission struct: tr_stat */
    data object RateDownload : TorrentFields<Speed>("rateDownload") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Speed? =
            torrentAccessorData.rateDownload.value
    }

    /**
     *
     * transmission struct: tr_stat */
    data object RateUpload : TorrentFields<Speed>("rateUpload") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Speed? =
            torrentAccessorData.rateUpload.value
    }

    /**
     *
     * transmission struct: tr_stat */
    data object RecheckProgress : TorrentFields<Double>("recheckProgress") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Double? =
            torrentAccessorData.recheckProgress.value
    }

    /**
     *
     * transmission struct: tr_stat */
    data object SecondsDownloading : TorrentFields<Int>("secondsDownloading") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.secondsDownloading.value
    }

    /**
     *
     * transmission struct: tr_stat */
    data object SecondsSeeding : TorrentFields<Int>("secondsSeeding") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.secondsSeeding.value
    }

    /**
     *
     * transmission struct: tr_torrent */
    data object SeedIdleLimit : TorrentFields<Int>("seedIdleLimit") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.seedIdleLimit.value
    }

    /**
     *
     * transmission struct: tr_inactivelimit */
    data object SeedIdleMode : TorrentFields<Int>("seedIdleMode") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.seedIdleMode.value
    }

    /**
     *
     * transmission struct: tr_torrent */
    data object SeedRatioLimit : TorrentFields<Double>("seedRatioLimit") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Double? =
            torrentAccessorData.seedRatioLimit.value
    }

    /**
     *
     * transmission struct: tr_ratiolimit */
    data object SeedRatioMode : TorrentFields<Int>("seedRatioMode") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.seedRatioMode.value
    }

    /**
     *
     * transmission struct: tr_torrent */
    data object SequentialDownload : TorrentFields<Boolean>("sequential_download") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Boolean? =
            torrentAccessorData.sequentialDownload.value
    }

    /**
     *
     * transmission struct: tr_stat */
    data object SizeWhenDone : TorrentFields<Int>("sizeWhenDone") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.sizeWhenDone.value
    }

    /**
     *
     * transmission struct: tr_stat */
    data object StartDate : TorrentFields<Instant>("startDate") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Instant? =
            torrentAccessorData.startDate.value
    }

    /**
     *
     * transmission struct: tr_stat */
    data object Status : TorrentFields<Int>("status") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.status.value
    }

    /**  */
    data object Trackers : TorrentFields<List<Int>>("trackers") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): List<Int>? =
            torrentAccessorData.trackers.value
    }

    /**
     *
     * transmission struct: string of announce URLs, one per line, with a blank line between tiers */
    data object TrackerList : TorrentFields<String>("trackerList") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): String? =
            torrentAccessorData.trackerList.value
    }

    /**  */
    data object TrackerStats : TorrentFields<List<Int>>("trackerStats") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): List<Int>? =
            torrentAccessorData.trackerStats.value
    }

    /**
     *
     * transmission struct: tr_torrent_view */
    data object TotalSize : TorrentFields<Int>("totalSize") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.totalSize.value
    }

    /**
     *
     * transmission struct: tr_info */
    data object TorrentFile : TorrentFields<String>("torrentFile") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): String? =
            torrentAccessorData.torrentFile.value
    }

    /**
     *
     * transmission struct: tr_stat */
    data object UploadedEver : TorrentFields<Int>("uploadedEver") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.uploadedEver.value
    }

    /**
     *
     * transmission struct: tr_torrent */
    data object UploadLimit : TorrentFields<Int>("uploadLimit") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.uploadLimit.value
    }

    /**
     *
     * transmission struct: tr_torrent */
    data object UploadLimited : TorrentFields<Boolean>("uploadLimited") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Boolean? =
            torrentAccessorData.uploadLimited.value
    }

    /**
     *
     * transmission struct: tr_stat */
    data object UploadRatio : TorrentFields<Double>("uploadRatio") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Double? =
            torrentAccessorData.uploadRatio.value
    }

    /**  */
    data object Wanted : TorrentFields<List<Int>>("wanted") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): List<Int>? =
            torrentAccessorData.wanted.value
    }

    /**
     *
     * transmission struct: tr_tracker_view */
    data object Webseeds : TorrentFields<List<String>>("webseeds") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): List<String>? =
            torrentAccessorData.webseeds.value
    }

    /**
     *
     * transmission struct: tr_stat */
    data object WebseedsSendingToUs : TorrentFields<Int>("webseedsSendingToUs") {
        override fun getValue(torrentAccessorData: TorrentAccessorData): Int? =
            torrentAccessorData.webseedsSendingToUs.value
    }
}

// table parsing sounds very difficult for kotlinx serialization
// so even though json parsing is less efficient, we'll have to do that

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
    val eta: Optional<Duration> = Optional.None(),
    /** property corresponding to [TorrentFields.EtaIdle] */
    val etaIdle: Optional<Duration> = Optional.None(),
    /** property corresponding to [TorrentFields.FileCount] */
    @SerialName("file-count")
    val fileCount: Optional<Int> = Optional.None(),
    /** property corresponding to [TorrentFields.Files] */
    val files: Optional<List<Int>> = Optional.None(),
    /** property corresponding to [TorrentFields.FileStats] */
    val fileStats: Optional<List<Int>> = Optional.None(),
    /** property corresponding to [TorrentFields.Group] */
    val group: Optional<String> = Optional.None(),
    /** property corresponding to [TorrentFields.HashString] */
    val hashString: Optional<String> = Optional.None(),
    /** property corresponding to [TorrentFields.HaveUnchecked] */
    val haveUnchecked: Optional<Int> = Optional.None(),
    /** property corresponding to [TorrentFields.HaveValid] */
    val haveValid: Optional<Int> = Optional.None(),
    /** property corresponding to [TorrentFields.HonorsSessionLimits] */
    val honorsSessionLimits: Optional<Boolean> = Optional.None(),
    /** property corresponding to [TorrentFields.Id] */
    val id: Optional<Int> = Optional.None(),
    /** property corresponding to [TorrentFields.IsFinished] */
    val isFinished: Optional<Boolean> = Optional.None(),
    /** property corresponding to [TorrentFields.IsPrivate] */
    val isPrivate: Optional<Boolean> = Optional.None(),
    /** property corresponding to [TorrentFields.IsStalled] */
    val isStalled: Optional<Boolean> = Optional.None(),
    /** property corresponding to [TorrentFields.Labels] */
    val labels: Optional<List<String>> = Optional.None(),
    /** property corresponding to [TorrentFields.LeftUntilDone] */
    val leftUntilDone: Optional<Int> = Optional.None(),
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
    val peers: Optional<List<Int>> = Optional.None(),
    /** property corresponding to [TorrentFields.PeersConnected] */
    val peersConnected: Optional<Int> = Optional.None(),
    /** property corresponding to [TorrentFields.PeersFrom] */
    val peersFrom: Optional<List<String>> = Optional.None(),
    /** property corresponding to [TorrentFields.PeersGettingFromUs] */
    val peersGettingFromUs: Optional<Int> = Optional.None(),
    /** property corresponding to [TorrentFields.PeersSendingToUs] */
    val peersSendingToUs: Optional<Int> = Optional.None(),
    /** property corresponding to [TorrentFields.PercentComplete] */
    val percentComplete: Optional<Double> = Optional.None(),
    /** property corresponding to [TorrentFields.PercentDone] */
    val percentDone: Optional<Double> = Optional.None(),
    /** property corresponding to [TorrentFields.Pieces] */
    val pieces: Optional<String> = Optional.None(),
    /** property corresponding to [TorrentFields.PieceCount] */
    val pieceCount: Optional<Int> = Optional.None(),
    /** property corresponding to [TorrentFields.PieceSize] */
    val pieceSize: Optional<Int> = Optional.None(),
    /** property corresponding to [TorrentFields.Priorities] */
    val priorities: Optional<List<Int>> = Optional.None(),
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
    val secondsDownloading: Optional<Int> = Optional.None(),
    /** property corresponding to [TorrentFields.SecondsSeeding] */
    val secondsSeeding: Optional<Int> = Optional.None(),
    /** property corresponding to [TorrentFields.SeedIdleLimit] */
    val seedIdleLimit: Optional<Int> = Optional.None(),
    /** property corresponding to [TorrentFields.SeedIdleMode] */
    val seedIdleMode: Optional<Int> = Optional.None(),
    /** property corresponding to [TorrentFields.SeedRatioLimit] */
    val seedRatioLimit: Optional<Double> = Optional.None(),
    /** property corresponding to [TorrentFields.SeedRatioMode] */
    val seedRatioMode: Optional<Int> = Optional.None(),
    /** property corresponding to [TorrentFields.SequentialDownload] */
    @SerialName("sequential_download")
    val sequentialDownload: Optional<Boolean> = Optional.None(),
    /** property corresponding to [TorrentFields.SizeWhenDone] */
    val sizeWhenDone: Optional<Int> = Optional.None(),
    /** property corresponding to [TorrentFields.StartDate] */
    val startDate: Optional<@Serializable(with = UnixTimeSerializer::class) Instant> = Optional.None(),
    /** property corresponding to [TorrentFields.Status] */
    val status: Optional<Int> = Optional.None(),
    /** property corresponding to [TorrentFields.Trackers] */
    val trackers: Optional<List<Int>> = Optional.None(),
    /** property corresponding to [TorrentFields.TrackerList] */
    val trackerList: Optional<String> = Optional.None(),
    /** property corresponding to [TorrentFields.TrackerStats] */
    val trackerStats: Optional<List<Int>> = Optional.None(),
    /** property corresponding to [TorrentFields.TotalSize] */
    val totalSize: Optional<Int> = Optional.None(),
    /** property corresponding to [TorrentFields.TorrentFile] */
    val torrentFile: Optional<String> = Optional.None(),
    /** property corresponding to [TorrentFields.UploadedEver] */
    val uploadedEver: Optional<Int> = Optional.None(),
    /** property corresponding to [TorrentFields.UploadLimit] */
    val uploadLimit: Optional<Int> = Optional.None(),
    /** property corresponding to [TorrentFields.UploadLimited] */
    val uploadLimited: Optional<Boolean> = Optional.None(),
    /** property corresponding to [TorrentFields.UploadRatio] */
    val uploadRatio: Optional<Double> = Optional.None(),
    /** property corresponding to [TorrentFields.Wanted] */
    val wanted: Optional<List<Int>> = Optional.None(),
    /** property corresponding to [TorrentFields.Webseeds] */
    val webseeds: Optional<List<String>> = Optional.None(),
    /** property corresponding to [TorrentFields.WebseedsSendingToUs] */
    val webseedsSendingToUs: Optional<Int> = Optional.None(),
) {
    fun debugString(): String {
        val properties = toString().substringAfter('(').substringBeforeLast(')')
            .split(", ")
        return properties.filter { !it.endsWith("=None") }
            .joinToString(prefix = "{", postfix = "}") {
                // remove the extraneous Some(...) surrounding the content
                it.substringBefore("=") + ":" +
                        it.substringAfter("Some(").dropLast(1)
            }
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
class TorrentAccessorResponse(val torrents: List<TorrentAccessorData>) :
    List<TorrentAccessorData> by torrents, RpcResponse

suspend fun RpcClient.getTorrentData(ids: TorrentIds, fields: List<TorrentFields<*>>): List<TorrentAccessorData> {
    return request(TorrentAccessorRequest(ids, fields.map { it.key })).torrents
}