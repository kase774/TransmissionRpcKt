package dev.kason.transrpc.low

import dev.kason.transrpc.data.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// implements chapter 3.2
// https://github.com/transmission/transmission/blob/main/docs/rpc-spec.md#32-torrent-mutator-torrent-set
@Serializable
data class TorrentSetRequest(
    /** this torrent's bandwidth priority */
    val bandwidthPriority: Optional<Priority> = Optional.None(),
    /** maximum download speed (KBps) */
    val downloadLimit: Optional<Speed> = Optional.None(),
    /** true if downloadLimit is honored */
    val downloadLimited: Optional<Boolean> = Optional.None(),
    /** indices of file(s) to not download */
    @SerialName("files-unwanted")
    val filesUnwanted: Optional<List<Int>> = Optional.None(),
    /** indices of file(s) to download */
    @SerialName("files-wanted")
    val filesWanted: Optional<List<Int>> = Optional.None(),
    /** The name of this torrent's bandwidth group */
    val group: Optional<String> = Optional.None(),
    /** true if session upload limits are honored */
    val honorsSessionLimits: Optional<Boolean> = Optional.None(),
    /** torrent list, as described in 3.1 */
    val ids: TorrentIds = TorrentIds.All,
    /** array of string labels */
    val labels: Optional<List<String>> = Optional.None(),
    /** new location of the torrent's content */
    val location: Optional<String> = Optional.None(),
    /** Maximum number of peers */
    @SerialName("peer-limit")
    val peerLimit: Optional<UInt> = Optional.None(),
    /** indices of high-priority file(s) */
    @SerialName("priority-high")
    val highPriorityFiles: Optional<List<Int>> = Optional.None(),
    /** indices of low-priority file(s) */
    @SerialName("priority-low")
    val lowPriorityFiles: Optional<List<Int>> = Optional.None(),
    /** indices of normal-priority file(s) */
    @SerialName("priority-normal")
    val normalPriorityFiles: Optional<List<Int>> = Optional.None(),
    /** position of this torrent in its queue [0...n) */
    val queuePosition: Optional<Int> = Optional.None(),
    /** number of minutes of idle time before we stop seeding. See [TorrentFields.SeedIdleLimit] */
    val seedIdleLimit: Optional<Int> = Optional.None(),
    /** which seeding inactivity to use. See [TorrentIdleMode] */
    val seedIdleMode: Optional<TorrentIdleMode> = Optional.None(),
    /** torrent-level seeding ratio */
    val seedRatioLimit: Optional<Double> = Optional.None(),
    /** which ratio to use. See tr_ratiolimit */
    val seedRatioMode: Optional<TorrentRatioLimit> = Optional.None(),
    /** download torrent pieces sequentially */
    @SerialName("sequential_download")
    val sequentialDownload: Optional<Boolean> = Optional.None(),
    /** string of announce URLs, one per line, and a blank line between tiers. */
    // todo later wrap this in a class
    val trackerList: Optional<String> = Optional.None(),
    /** maximum upload speed (KBps) */
    val uploadLimit: Optional<Int> = Optional.None(),
    /** true if uploadLimit is honored */
    val uploadLimited: Optional<Boolean> = Optional.None(),
) : RpcRequest<NullResponse>() {
    override val method: String
        get() = "torrent-set"
}

/** Public for torrent set since it's easier to use the class sometimes */
suspend fun RpcClient.sendTorrentSetProperties(setRequest: TorrentSetRequest) =
    request(setRequest)

/** Sets the properties */
suspend fun RpcClient.torrentSetProperties(
    ids: TorrentIds,
    /** this torrent's bandwidth priority */
    bandwidthPriority: Priority? = null,
    /** maximum download speed (KBps) */
    downloadLimit: Speed? = null,
    /** true if downloadLimit is honored */
    downloadLimited: Boolean? = null,
    /** indices of file(s) to not download */
    filesUnwanted: List<Int>? = null,
    /** indices of file(s) to download */
    filesWanted: List<Int>? = null,
    /** The name of this torrent's bandwidth group */
    group: String? = null,
    /** true if session upload limits are honored */
    honorsSessionLimits: Boolean? = null,
    /** array of string labels */
    labels: List<String>? = null,
    /** new location of the torrent's content */
    location: String? = null,
    /** Maximum number of peers */
    peerLimit: UInt? = null,
    /** indices of high-priority file(s) */
    highPriorityFiles: List<Int>? = null,
    /** indices of low-priority file(s) */
    lowPriorityFiles: List<Int>? = null,
    /** indices of normal-priority file(s) */
    normalPriorityFiles: List<Int>? = null,
    /** position of this torrent in its queue [0...n) */
    queuePosition: Int? = null,
    /** torrent-level number of minutes of seeding inactivity */
    seedIdleLimit: Int? = null,
    /** which seeding inactivity to use. See tr_idlelimit */
    seedIdleMode: TorrentIdleMode? = null,
    /** torrent-level seeding ratio */
    seedRatioLimit: Double? = null,
    /** which ratio to use. See tr_ratiolimit */
    seedRatioMode: TorrentRatioLimit? = null,
    /** download torrent pieces sequentially */
    sequentialDownload: Boolean? = null,
    /** string of announce URLs, one per line, and a blank line between tiers. */
    trackerList: String? = null,
    /** maximum upload speed (KBps) */
    uploadLimit: Int? = null,
    /** true if uploadLimit is honored */
    uploadLimited: Boolean? = null
) {
    sendTorrentSetProperties(
        TorrentSetRequest(
            Optional(bandwidthPriority),
            Optional(downloadLimit),
            Optional(downloadLimited),
            Optional(filesUnwanted),
            Optional(filesWanted),
            Optional(group),
            Optional(honorsSessionLimits),
            ids,
            Optional(labels),
            Optional(location),
            Optional(peerLimit),
            Optional(highPriorityFiles),
            Optional(lowPriorityFiles),
            Optional(normalPriorityFiles),
            Optional(queuePosition),
            Optional(seedIdleLimit),
            Optional(seedIdleMode),
            Optional(seedRatioLimit),
            Optional(seedRatioMode),
            Optional(sequentialDownload),
            Optional(trackerList),
            Optional(uploadLimit),
            Optional(uploadLimited),
        )
    )
}