package dev.kason.transrpc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties

// raw deserialize
// everything is nullable so that we dont have to query all fields at once

// relevant links
// tr_stat https://github.com/transmission/transmission/blob/main/libtransmission/transmission.h
// tr_torrent https://github.com/transmission/transmission/blob/main/libtransmission/torrent.h
@Serializable
internal data class TorrentData(
    val errorString: String? = null,
    val sizeWhenDone: ULong? = null,
    val leftUntilDone: ULong? = null,
    val desiredAvailable: ULong? = null,
    val corruptEver: ULong? = null,
    val uploadedEver: ULong? = null,
    val downloadedEver: ULong? = null,
    val haveValid: ULong? = null,
    val haveUnchecked: ULong? = null,
    // these longs represent a time_t struct; seconds since epoch start
    // a long should be fine...; remember to cast to correct form for actual Torrent class
    val addedDate: Long? = null,
    val doneDate: Long? = null,
    val startDate: Long? = null,
    val activityDate: Long? = null,
    val editDate: Long? = null,
    val recheckProgress: Double? = null,
    val percentComplete: Double? = null,
    val metadataPercentComplete: Double? = null,
    val percentDone: Double? = null,
    val seedRatioPercentDone: Double? = null,
    // up to here taken from tr_stat struct in https://github.com/transmission/transmission/blob/main/libtransmission/transmission.h
    // rest of props are taken from ts_rpc list of structs (since they don't align perfectly)
    val secondsDownloading: Long? = null,
    val secondsSeeding: Long? = null,
    val seedIdleLimit: Long? = null,
    val availability: Array<Int>? = null,
    val bandwidthPriority: Byte? = null,
    val comment: String? = null,
    val creator: String? = null,
    val dateCreated: Long? = null,
    val downloadDir: String? = null,
    val downloadLimit: Int? = null,
    val downloadLimited: Boolean? = null,
    val error: Int? = null,
    val eta: Long? = null,
    val etaIdle: Long? = null,
    @SerialName("file-count")
    val fileCount: Int? = null,
    val files: List<FileData>? = null,
    val fileStats: List<FileStatsData>? = null,
    val group: String? = null,
    val hashString: String? = null,
    val honorsSessionLimits: Boolean? = null,
    val id: Int? = null,
    val isFinished: Boolean? = null,
    val isPrivate: Boolean? = null,
    val isStalled: Boolean? = null,
    val labels: List<String>? = null,
    val magnetLink: String? = null,
    val manualAnnounceTime: Long? = null,
    val maxConnectedPeers: Int? = null,
    val name: String? = null,
    @SerialName("peer-limit")
    val peerLimit: Int? = null,
    val peers: List<PeerData>? = null,
    val peersConnected: Int? = null,
    val peersFrom: PeersFromData? = null,
    val peersGettingFromUs: Int? = null,
    val peersSendingToUs: Int? = null,
    val pieces: String? = null,
    val pieceCount: Long? = null,
    val pieceSize: Long? = null,
    val priorities: Array<Byte>? = null,
    @SerialName("primary-mime-type")
    val primaryMimeType: String? = null,
    val queuePosition: Int? = null,
    val rateDownload: Long? = null,
    val rateUpload: Long? = null,
    val seedIdleMode: Int? = null,
    val seedRatioMode: Int? = null,
    val seedRatioLimit: Int? = null,
    @SerialName("sequential_download")
    val sequentialDownload: Boolean? = null,
    val status: Int? = null,
    val trackers: List<TrackerData>? = null,
    val trackerList: String? = null,
    val trackerStats: List<TrackerStatsData>? = null,
    val totalSize: Long? = null,
    val torrentFile: String? = null,
    val uploadLimit: ULong? = null,
    val uploadRatio: Double? = null,
    val wanted: List<Byte>? = null,
    val webseeds: List<String>? = null,
    val webseedsSendingToUs: Int? = null,
) {

    @Serializable
    data class FileData(
        val bytesCompleted: ULong,
        val length: ULong,
        val name: String,
        val begin_piece: Int,
        val end_piece: Int
    )

    @Serializable
    data class FileStatsData(
        val bytesCompleted: ULong,
        val wanted: Boolean,
        val priority: Int
    )

    // tr_peer_stat
    // https://github.com/transmission/transmission/blob/main/libtransmission/transmission.h 1172
    @Serializable
    data class PeerData(
        val address: String,
        val clientName: String,
        val clientIsChocked: Boolean,
        val clientIsInterested: Boolean,
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
        val rateToClient: Long,
        val rateToPeer: Long
    )

    @Serializable
    data class PeersFromData(
        val fromCache: Int,
        val fromDht: Int,
        val fromIncoming: Int,
        val fromLpd: Int,
        val fromLtep: Int,
        val fromPex: Int,
        val fromTracker: Int
    )

    @Serializable
    data class TrackerData(
        val announce: String,
        val id: Int,
        val scrape: String,
        val sitename: String,
        val tier: Int
    )

    @Serializable
    data class TrackerStatsData(
        val announce: String,
        val announceState: Int,
        val downloadCount: Int,
        val hasAnnounced: Boolean,
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
        val scrapeState: Int,
        val seederCount: Int,
        val sitename: String,
        val tier: Int
    )

}
