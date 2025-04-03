@file:Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")

package dev.kason.transrpc.low

import dev.kason.transrpc.data.LocalTimeMinuteSerializer
import dev.kason.transrpc.data.Optional
import dev.kason.transrpc.data.Speed
import kotlinx.datetime.LocalTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// 4.1 and 4.2
sealed class SessionFields<T : Any>(
    internal val key: String,
    val isMutable: Boolean
) {
    /** max global download speed (KBps) */
    data object AltSpeedDown : SessionFields<Speed>("alt-speed-down", true)

    /** true means use the alt speeds */
    data object AltSpeedEnabled : SessionFields<Boolean>("alt-speed-enabled", true)

    /** when to turn on alt speeds (units: minutes after midnight) */
    data object AltSpeedTimeBegin : SessionFields<LocalTime>("alt-speed-time-begin", true)

    /** what day(s) to turn on alt speeds (look at tr_sched_day) */
    data object AltSpeedTimeDay : SessionFields<TrSpeedSchedule>("alt-speed-time-day", true)

    /** true means the scheduled on/off times are used */
    data object AltSpeedTimeEnabled : SessionFields<Boolean>("alt-speed-time-enabled", true)

    /** when to turn off alt speeds (units: same) */
    data object AltSpeedTimeEnd : SessionFields<LocalTime>("alt-speed-time-end", true)

    /** max global upload speed (KBps) */
    data object AltSpeedUp : SessionFields<Speed>("alt-speed-up", true)

    /** true means enabled */
    data object BlocklistEnabled : SessionFields<Boolean>("blocklist-enabled", true)

    /** number of rules in the blocklist */
    data object BlocklistSize : SessionFields<Int>("blocklist-size", false)

    /** location of the blocklist to use for blocklist-update */
    data object BlocklistUrl : SessionFields<String>("blocklist-url", true)

    /** maximum size of the disk cache (MB) */
    // too lazy to write a serializer for byte count for this qwq
    data object CacheSizeMb : SessionFields<Int>("cache-size-mb", true)

    /** location of transmission's configuration directory */
    data object ConfigDir : SessionFields<String>("config-dir", false)

    /** announce URLs, one per line, and a blank line between tiers. */
    data object DefaultTrackers : SessionFields<String>("default-trackers", true)

    /** true means allow DHT in public torrents */
    data object DhtEnabled : SessionFields<Boolean>("dht-enabled", true)

    /** default path to download torrents */
    data object DownloadDir : SessionFields<String>("download-dir", true)

    /** if true, limit how many torrents can be downloaded at once */
    data object DownloadQueueEnabled : SessionFields<Boolean>("download-queue-enabled", true)

    /** max number of torrents to download at once (see download-queue-enabled) */
    data object DownloadQueueSize : SessionFields<Int>("download-queue-size", true)

    /** required, preferred, tolerated */
    data object Encryption : SessionFields<SessionEncryption>("encryption", true)

    /** true if the seeding inactivity limit is honored by default */
    data object IdleSeedingLimitEnabled : SessionFields<Boolean>("idle-seeding-limit-enabled", true)

    /** torrents we're seeding will be stopped if they're idle for this long */
    data object IdleSeedingLimit : SessionFields<Int>("idle-seeding-limit", true)

    /** true means keep torrents in incomplete-dir until done */
    data object IncompleteDirEnabled : SessionFields<Boolean>("incomplete-dir-enabled", true)

    /** path for incomplete torrents, when enabled */
    data object IncompleteDir : SessionFields<String>("incomplete-dir", true)

    /** true means allow Local Peer Discovery in public torrents */
    data object LpdEnabled : SessionFields<Boolean>("lpd-enabled", true)

    /** maximum global number of peers */
    data object PeerLimitGlobal : SessionFields<Int>("peer-limit-global", true)

    /** maximum global number of peers */
    data object PeerLimitPerTorrent : SessionFields<Int>("peer-limit-per-torrent", true)

    /** true means pick a random peer port on launch */
    data object PeerPortRandomOnStart : SessionFields<Boolean>("peer-port-random-on-start", true)

    /** port number */
    data object PeerPort : SessionFields<Int>("peer-port", true)

    /** true means allow PEX in public torrents */
    data object PexEnabled : SessionFields<Boolean>("pex-enabled", true)

    /** true means ask upstream router to forward the configured peer port to transmission using UPnP or NAT-PMP */
    data object PortForwardingEnabled : SessionFields<Boolean>("port-forwarding-enabled", true)

    /** whether or not to consider idle torrents as stalled */
    data object QueueStalledEnabled : SessionFields<Boolean>("queue-stalled-enabled", true)

    /** torrents that are idle for N minuets aren't counted toward seed-queue-size or download-queue-size */
    data object QueueStalledMinutes : SessionFields<Int>("queue-stalled-minutes", true)

    /** true means append .part to incomplete files */
    data object RenamePartialFiles : SessionFields<Boolean>("rename-partial-files", true)

    /** the number of outstanding block requests a peer is allowed to queue in the client */
    data object Reqq : SessionFields<Int>("reqq", true)

    /** the minimum RPC API version supported */
    data object RpcVersionMinimum : SessionFields<Int>("rpc-version-minimum", false)

    /** the current RPC API version in a semver-compatible string */
    data object RpcVersionSemver : SessionFields<String>("rpc-version-semver", false)

    /** the current RPC API version */
    data object RpcVersion : SessionFields<Int>("rpc-version", false)

    /** whether or not to call the added script */
    data object ScriptTorrentAddedEnabled : SessionFields<Boolean>("script-torrent-added-enabled", true)

    /** filename of the script to run */
    data object ScriptTorrentAddedFilename : SessionFields<String>("script-torrent-added-filename", true)

    /** whether or not to call the done script */
    data object ScriptTorrentDoneEnabled : SessionFields<Boolean>("script-torrent-done-enabled", true)

    /** filename of the script to run */
    data object ScriptTorrentDoneFilename : SessionFields<String>("script-torrent-done-filename", true)

    /** whether or not to call the seeding-done script */
    data object ScriptTorrentDoneSeedingEnabled : SessionFields<Boolean>("script-torrent-done-seeding-enabled", true)

    /** filename of the script to run */
    data object ScriptTorrentDoneSeedingFilename : SessionFields<String>("script-torrent-done-seeding-filename", true)

    /** if true, limit how many torrents can be uploaded at once */
    data object SeedQueueEnabled : SessionFields<Boolean>("seed-queue-enabled", true)

    /** max number of torrents to uploaded at once (see seed-queue-enabled) */
    data object SeedQueueSize : SessionFields<Int>("seed-queue-size", true)

    /** the default seed ratio for torrents to use */
    data object SeedRatioLimit : SessionFields<Double>("seedRatioLimit", true)

    /** true if seedRatioLimit is honored by default */
    data object SeedRatioLimited : SessionFields<Boolean>("seedRatioLimited", true)

    /** true means sequential download is enabled by default for added torrents */
    data object SequentialDownload : SessionFields<Boolean>("sequential_download", true)

    /** the current X-Transmission-Session-Id value */
    data object SessionId : SessionFields<String>("session-id", false)

    /** true means enabled */
    data object SpeedLimitDownEnabled : SessionFields<Boolean>("speed-limit-down-enabled", true)

    /** max global download speed (KBps) */
    data object SpeedLimitDown : SessionFields<Speed>("speed-limit-down", true)

    /** true means enabled */
    data object SpeedLimitUpEnabled : SessionFields<Boolean>("speed-limit-up-enabled", true)

    /** max global upload speed (KBps) */
    data object SpeedLimitUp : SessionFields<Speed>("speed-limit-up", true)

    /** true means added torrents will be started right away */
    data object StartAddedTorrents : SessionFields<Boolean>("start-added-torrents", true)

    /** true means the .torrent file of added torrents will be deleted */
    data object TrashOriginalTorrentFiles : SessionFields<Boolean>("trash-original-torrent-files", true)

    /** true means allow UTP */
    data object UtpEnabled : SessionFields<Boolean>("utp-enabled", true)

    /** long version string */
    data object Version : SessionFields<String>("version", false)
}

@Serializable
data class SessionAccessorData(
    /** property corresponding to [SessionFields.AltSpeedDown] */
    @SerialName("alt-speed-down")
    val altSpeedDown: Optional<Speed> = Optional.None(),
    /** property corresponding to [SessionFields.AltSpeedEnabled] */
    @SerialName("alt-speed-enabled")
    val altSpeedEnabled: Optional<Boolean> = Optional.None(),
    /** property corresponding to [SessionFields.AltSpeedTimeBegin] */
    @SerialName("alt-speed-time-begin")
    val altSpeedTimeBegin: Optional<@Serializable(with = LocalTimeMinuteSerializer::class) LocalTime> = Optional.None(),
    /** property corresponding to [SessionFields.AltSpeedTimeDay] */
    @SerialName("alt-speed-time-day")
    val altSpeedTimeDay: Optional<TrSpeedSchedule> = Optional.None(),
    /** property corresponding to [SessionFields.AltSpeedTimeEnabled] */
    @SerialName("alt-speed-time-enabled")
    val altSpeedTimeEnabled: Optional<Boolean> = Optional.None(),
    /** property corresponding to [SessionFields.AltSpeedTimeEnd] */
    @SerialName("alt-speed-time-end")
    val altSpeedTimeEnd: Optional<@Serializable(with = LocalTimeMinuteSerializer::class) LocalTime> = Optional.None(),
    /** property corresponding to [SessionFields.AltSpeedUp] */
    @SerialName("alt-speed-up")
    val altSpeedUp: Optional<Speed> = Optional.None(),
    /** property corresponding to [SessionFields.BlocklistEnabled] */
    @SerialName("blocklist-enabled")
    val blocklistEnabled: Optional<Boolean> = Optional.None(),
    /** property corresponding to [SessionFields.BlocklistSize] */
    @SerialName("blocklist-size")
    val blocklistSize: Optional<Int> = Optional.None(),
    /** property corresponding to [SessionFields.BlocklistUrl] */
    @SerialName("blocklist-url")
    val blocklistUrl: Optional<String> = Optional.None(),
    /** property corresponding to [SessionFields.CacheSizeMb] */
    @SerialName("cache-size-mb")
    val cacheSizeMb: Optional<Int> = Optional.None(),
    /** property corresponding to [SessionFields.ConfigDir] */
    @SerialName("config-dir")
    val configDir: Optional<String> = Optional.None(),
    /** property corresponding to [SessionFields.DefaultTrackers] */
    @SerialName("default-trackers")
    val defaultTrackers: Optional<String> = Optional.None(),
    /** property corresponding to [SessionFields.DhtEnabled] */
    @SerialName("dht-enabled")
    val dhtEnabled: Optional<Boolean> = Optional.None(),
    /** property corresponding to [SessionFields.DownloadDir] */
    @SerialName("download-dir")
    val downloadDir: Optional<String> = Optional.None(),
    /** property corresponding to [SessionFields.DownloadQueueEnabled] */
    @SerialName("download-queue-enabled")
    val downloadQueueEnabled: Optional<Boolean> = Optional.None(),
    /** property corresponding to [SessionFields.DownloadQueueSize] */
    @SerialName("download-queue-size")
    val downloadQueueSize: Optional<Int> = Optional.None(),
    /** property corresponding to [SessionFields.Encryption] */
    val encryption: Optional<SessionEncryption> = Optional.None(),
    /** property corresponding to [SessionFields.IdleSeedingLimitEnabled] */
    @SerialName("idle-seeding-limit-enabled")
    val idleSeedingLimitEnabled: Optional<Boolean> = Optional.None(),
    /** property corresponding to [SessionFields.IdleSeedingLimit] */
    @SerialName("idle-seeding-limit")
    val idleSeedingLimit: Optional<Int> = Optional.None(),
    /** property corresponding to [SessionFields.IncompleteDirEnabled] */
    @SerialName("incomplete-dir-enabled")
    val incompleteDirEnabled: Optional<Boolean> = Optional.None(),
    /** property corresponding to [SessionFields.IncompleteDir] */
    @SerialName("incomplete-dir")
    val incompleteDir: Optional<String> = Optional.None(),
    /** property corresponding to [SessionFields.LpdEnabled] */
    @SerialName("lpd-enabled")
    val lpdEnabled: Optional<Boolean> = Optional.None(),
    /** property corresponding to [SessionFields.PeerLimitGlobal] */
    @SerialName("peer-limit-global")
    val peerLimitGlobal: Optional<Int> = Optional.None(),
    /** property corresponding to [SessionFields.PeerLimitPerTorrent] */
    @SerialName("peer-limit-per-torrent")
    val peerLimitPerTorrent: Optional<Int> = Optional.None(),
    /** property corresponding to [SessionFields.PeerPortRandomOnStart] */
    @SerialName("peer-port-random-on-start")
    val peerPortRandomOnStart: Optional<Boolean> = Optional.None(),
    /** property corresponding to [SessionFields.PeerPort] */
    @SerialName("peer-port")
    val peerPort: Optional<Int> = Optional.None(),
    /** property corresponding to [SessionFields.PexEnabled] */
    @SerialName("pex-enabled")
    val pexEnabled: Optional<Boolean> = Optional.None(),
    /** property corresponding to [SessionFields.PortForwardingEnabled] */
    @SerialName("port-forwarding-enabled")
    val portForwardingEnabled: Optional<Boolean> = Optional.None(),
    /** property corresponding to [SessionFields.QueueStalledEnabled] */
    @SerialName("queue-stalled-enabled")
    val queueStalledEnabled: Optional<Boolean> = Optional.None(),
    /** property corresponding to [SessionFields.QueueStalledMinutes] */
    @SerialName("queue-stalled-minutes")
    val queueStalledMinutes: Optional<Int> = Optional.None(),
    /** property corresponding to [SessionFields.RenamePartialFiles] */
    @SerialName("rename-partial-files")
    val renamePartialFiles: Optional<Boolean> = Optional.None(),
    /** property corresponding to [SessionFields.Reqq] */
    val reqq: Optional<Int> = Optional.None(),
    /** property corresponding to [SessionFields.RpcVersionMinimum] */
    @SerialName("rpc-version-minimum")
    val rpcVersionMinimum: Optional<Int> = Optional.None(),
    /** property corresponding to [SessionFields.RpcVersionSemver] */
    @SerialName("rpc-version-semver")
    val rpcVersionSemver: Optional<String> = Optional.None(),
    /** property corresponding to [SessionFields.RpcVersion] */
    @SerialName("rpc-version")
    val rpcVersion: Optional<Int> = Optional.None(),
    /** property corresponding to [SessionFields.ScriptTorrentAddedEnabled] */
    @SerialName("script-torrent-added-enabled")
    val scriptTorrentAddedEnabled: Optional<Boolean> = Optional.None(),
    /** property corresponding to [SessionFields.ScriptTorrentAddedFilename] */
    @SerialName("script-torrent-added-filename")
    val scriptTorrentAddedFilename: Optional<String> = Optional.None(),
    /** property corresponding to [SessionFields.ScriptTorrentDoneEnabled] */
    @SerialName("script-torrent-done-enabled")
    val scriptTorrentDoneEnabled: Optional<Boolean> = Optional.None(),
    /** property corresponding to [SessionFields.ScriptTorrentDoneFilename] */
    @SerialName("script-torrent-done-filename")
    val scriptTorrentDoneFilename: Optional<String> = Optional.None(),
    /** property corresponding to [SessionFields.ScriptTorrentDoneSeedingEnabled] */
    @SerialName("script-torrent-done-seeding-enabled")
    val scriptTorrentDoneSeedingEnabled: Optional<Boolean> = Optional.None(),
    /** property corresponding to [SessionFields.ScriptTorrentDoneSeedingFilename] */
    @SerialName("script-torrent-done-seeding-filename")
    val scriptTorrentDoneSeedingFilename: Optional<String> = Optional.None(),
    /** property corresponding to [SessionFields.SeedQueueEnabled] */
    @SerialName("seed-queue-enabled")
    val seedQueueEnabled: Optional<Boolean> = Optional.None(),
    /** property corresponding to [SessionFields.SeedQueueSize] */
    @SerialName("seed-queue-size")
    val seedQueueSize: Optional<Int> = Optional.None(),
    /** property corresponding to [SessionFields.SeedRatioLimit] */
    val seedRatioLimit: Optional<Double> = Optional.None(),
    /** property corresponding to [SessionFields.SeedRatioLimited] */
    val seedRatioLimited: Optional<Boolean> = Optional.None(),
    /** property corresponding to [SessionFields.SequentialDownload] */
    @SerialName("sequential_download")
    val sequentialDownload: Optional<Boolean> = Optional.None(),
    /** property corresponding to [SessionFields.SessionId] */
    @SerialName("session-id")
    val sessionId: Optional<String> = Optional.None(),
    /** property corresponding to [SessionFields.SpeedLimitDownEnabled] */
    @SerialName("speed-limit-down-enabled")
    val speedLimitDownEnabled: Optional<Boolean> = Optional.None(),
    /** property corresponding to [SessionFields.SpeedLimitDown] */
    @SerialName("speed-limit-down")
    val speedLimitDown: Optional<Speed> = Optional.None(),
    /** property corresponding to [SessionFields.SpeedLimitUpEnabled] */
    @SerialName("speed-limit-up-enabled")
    val speedLimitUpEnabled: Optional<Boolean> = Optional.None(),
    /** property corresponding to [SessionFields.SpeedLimitUp] */
    @SerialName("speed-limit-up")
    val speedLimitUp: Optional<Speed> = Optional.None(),
    /** property corresponding to [SessionFields.StartAddedTorrents] */
    @SerialName("start-added-torrents")
    val startAddedTorrents: Optional<Boolean> = Optional.None(),
    /** property corresponding to [SessionFields.TrashOriginalTorrentFiles] */
    @SerialName("trash-original-torrent-files")
    val trashOriginalTorrentFiles: Optional<Boolean> = Optional.None(),
    /** property corresponding to [SessionFields.UtpEnabled] */
    @SerialName("utp-enabled")
    val utpEnabled: Optional<Boolean> = Optional.None(),
    /** property corresponding to [SessionFields.Version] */
    val version: Optional<String> = Optional.None(),
) : RpcResponse, RpcRequest<NullResponse>() {
    // its also a request bc we'll be sending one of these as a request to set
    // session properties
    override val method: String
        get() = "session-set"
}


// lwk should've done this extension property pattern from the start
// for Torrents instead of that wacky getValue() thing
operator fun <T : Any> SessionAccessorData.get(field: SessionFields<T>): T? {
    return when (field) {
        is SessionFields.AltSpeedDown -> (altSpeedDown.value)
        is SessionFields.AltSpeedEnabled -> (altSpeedEnabled.value)
        is SessionFields.AltSpeedTimeBegin -> (altSpeedTimeBegin.value)
        is SessionFields.AltSpeedTimeDay -> (altSpeedTimeDay.value)
        is SessionFields.AltSpeedTimeEnabled -> (altSpeedTimeEnabled.value)
        is SessionFields.AltSpeedTimeEnd -> (altSpeedTimeEnd.value)
        is SessionFields.AltSpeedUp -> (altSpeedUp.value)
        is SessionFields.BlocklistEnabled -> (blocklistEnabled.value)
        is SessionFields.BlocklistSize -> (blocklistSize.value)
        is SessionFields.BlocklistUrl -> (blocklistUrl.value)
        is SessionFields.CacheSizeMb -> (cacheSizeMb.value)
        is SessionFields.ConfigDir -> (configDir.value)
        is SessionFields.DefaultTrackers -> (defaultTrackers.value)
        is SessionFields.DhtEnabled -> (dhtEnabled.value)
        is SessionFields.DownloadDir -> (downloadDir.value)
        is SessionFields.DownloadQueueEnabled -> (downloadQueueEnabled.value)
        is SessionFields.DownloadQueueSize -> (downloadQueueSize.value)
        is SessionFields.Encryption -> (encryption.value)
        is SessionFields.IdleSeedingLimitEnabled -> (idleSeedingLimitEnabled.value)
        is SessionFields.IdleSeedingLimit -> (idleSeedingLimit.value)
        is SessionFields.IncompleteDirEnabled -> (incompleteDirEnabled.value)
        is SessionFields.IncompleteDir -> (incompleteDir.value)
        is SessionFields.LpdEnabled -> (lpdEnabled.value)
        is SessionFields.PeerLimitGlobal -> (peerLimitGlobal.value)
        is SessionFields.PeerLimitPerTorrent -> (peerLimitPerTorrent.value)
        is SessionFields.PeerPortRandomOnStart -> (peerPortRandomOnStart.value)
        is SessionFields.PeerPort -> (peerPort.value)
        is SessionFields.PexEnabled -> (pexEnabled.value)
        is SessionFields.PortForwardingEnabled -> (portForwardingEnabled.value)
        is SessionFields.QueueStalledEnabled -> (queueStalledEnabled.value)
        is SessionFields.QueueStalledMinutes -> (queueStalledMinutes.value)
        is SessionFields.RenamePartialFiles -> (renamePartialFiles.value)
        is SessionFields.Reqq -> (reqq.value)
        is SessionFields.RpcVersionMinimum -> (rpcVersionMinimum.value)
        is SessionFields.RpcVersionSemver -> (rpcVersionSemver.value)
        is SessionFields.RpcVersion -> (rpcVersion.value)
        is SessionFields.ScriptTorrentAddedEnabled -> (scriptTorrentAddedEnabled.value)
        is SessionFields.ScriptTorrentAddedFilename -> (scriptTorrentAddedFilename.value)
        is SessionFields.ScriptTorrentDoneEnabled -> (scriptTorrentDoneEnabled.value)
        is SessionFields.ScriptTorrentDoneFilename -> (scriptTorrentDoneFilename.value)
        is SessionFields.ScriptTorrentDoneSeedingEnabled -> (scriptTorrentDoneEnabled.value)
        is SessionFields.ScriptTorrentDoneSeedingFilename -> (scriptTorrentDoneSeedingFilename.value)
        is SessionFields.SeedQueueEnabled -> (seedQueueEnabled.value)
        is SessionFields.SeedQueueSize -> (seedQueueSize.value)
        is SessionFields.SeedRatioLimit -> (seedRatioLimit.value)
        is SessionFields.SeedRatioLimited -> (seedRatioLimited.value)
        is SessionFields.SequentialDownload -> (sequentialDownload.value)
        is SessionFields.SessionId -> (sessionId.value)
        is SessionFields.SpeedLimitDownEnabled -> (speedLimitDownEnabled.value)
        is SessionFields.SpeedLimitDown -> (speedLimitDown.value)
        is SessionFields.SpeedLimitUpEnabled -> (speedLimitUpEnabled.value)
        is SessionFields.SpeedLimitUp -> (speedLimitUp.value)
        is SessionFields.StartAddedTorrents -> (startAddedTorrents.value)
        is SessionFields.TrashOriginalTorrentFiles -> (trashOriginalTorrentFiles.value)
        is SessionFields.UtpEnabled -> (utpEnabled.value)
        is SessionFields.Version -> (version.value)
    } as T?
}

@Serializable
internal data class SessionAccessorRequest(
    val fields: List<String>? = null
) : RpcRequest<SessionAccessorData>() {
    override val method: String
        get() = "session-get"
}

/** Gets the session data for the given fields in the form of a [SessionAccessorData] object with the fields
 * that are supplied filled out (in a similar fashion to [RpcClient.getTorrentData]). If the fields
 * list is null, this function will return a [SessionAccessorData] will all the fields filled out. */
suspend fun RpcClient.getSessionData(fields: List<SessionFields<*>>? = null): SessionAccessorData {
    val request = SessionAccessorRequest(fields?.map { it.key })
    return request(request)
}

private val nonMutableProperties = SessionFields::class.sealedSubclasses
    .map { it.objectInstance!! }
    .filter { !it.isMutable }

/** Sets the session data to the properties in the given [SessionAccessorData]. Note that not
 * all properties can be set:
 *     blocklist-size
 *     config-dir
 *     rpc-version-minimum,
 *     rpc-version-semver
 *     rpc-version
 *     session-id
 *     units
 *     version
 *
 *   can't be set.
 * */
suspend fun RpcClient.setSessionData(sessionAccessorData: SessionAccessorData) {
    val notMutableButSet = nonMutableProperties.filter { sessionAccessorData[it] != null }
    if (notMutableButSet.isNotEmpty()) {
        logger.warn { "property(s) $notMutableButSet are set in session set request but they aren't mutable" }
    }
    request(sessionAccessorData)
}

/** Sets the session data to the given parameters
 *
 * Params:
 * @param [altSpeedDown] max global download speed (KBps)
 * @param [altSpeedEnabled] true means use the alt speeds
 * @param [altSpeedTimeBegin] when to turn on alt speeds (units: minutes after midnight)
 * @param [altSpeedTimeDay] what day(s) to turn on alt speeds (look at tr_sched_day)
 * @param [altSpeedTimeEnabled] true means the scheduled on/off times are used
 * @param [altSpeedTimeEnd] when to turn off alt speeds (units: same)
 * @param [altSpeedUp] max global upload speed (KBps)
 * @param [blocklistEnabled] true means enabled
 * @param [blocklistUrl] location of the blocklist to use for blocklist-update
 * @param [cacheSizeMb] maximum size of the disk cache (MB)
 * @param [defaultTrackers] announce URLs, one per line, and a blank line between tiers.
 * @param [dhtEnabled] true means allow DHT in public torrents
 * @param [downloadDir] default path to download torrents
 * @param [downloadQueueEnabled] if true, limit how many torrents can be downloaded at once
 * @param [downloadQueueSize] max number of torrents to download at once (see download-queue-enabled)
 * @param [encryption] required, preferred, tolerated
 * @param [idleSeedingLimitEnabled] true if the seeding inactivity limit is honored by default
 * @param [idleSeedingLimit] torrents we're seeding will be stopped if they're idle for this long
 * @param [incompleteDirEnabled] true means keep torrents in incomplete-dir until done
 * @param [incompleteDir] path for incomplete torrents, when enabled
 * @param [lpdEnabled] true means allow Local Peer Discovery in public torrents
 * @param [peerLimitGlobal] maximum global number of peers
 * @param [peerLimitPerTorrent] maximum global number of peers
 * @param [peerPortRandomOnStart] true means pick a random peer port on launch
 * @param [peerPort] port number
 * @param [pexEnabled] true means allow PEX in public torrents
 * @param [portForwardingEnabled] true means ask upstream router to forward the configured peer port to transmission using UPnP or NAT-PMP
 * @param [queueStalledEnabled] whether or not to consider idle torrents as stalled
 * @param [queueStalledMinutes] torrents that are idle for N minutes aren't counted toward seed-queue-size or download-queue-size
 * @param [renamePartialFiles] true means append .part to incomplete files
 * @param [reqq] the number of outstanding block requests a peer is allowed to queue in the client
 * @param [scriptTorrentAddedEnabled] whether or not to call the added script
 * @param [scriptTorrentAddedFilename] filename of the script to run
 * @param [scriptTorrentDoneEnabled] whether or not to call the done script
 * @param [scriptTorrentDoneFilename] filename of the script to run
 * @param [scriptTorrentDoneSeedingEnabled] whether or not to call the seeding-done script
 * @param [scriptTorrentDoneSeedingFilename] filename of the script to run
 * @param [seedQueueEnabled] if true, limit how many torrents can be uploaded at once
 * @param [seedQueueSize] max number of torrents to uploaded at once (see seed-queue-enabled)
 * @param [seedRatioLimit] the default seed ratio for torrents to use
 * @param [seedRatioLimited] true if seedRatioLimit is honored by default
 * @param [sequentialDownload] true means sequential download is enabled by default for added torrents
 * @param [speedLimitDownEnabled] true means enabled
 * @param [speedLimitDown] max global download speed (KBps)
 * @param [speedLimitUpEnabled] true means enabled
 * @param [speedLimitUp] max global upload speed (KBps)
 * @param [startAddedTorrents] true means added torrents will be started right away
 * @param [trashOriginalTorrentFiles] true means the .torrent file of added torrents will be deleted
 * @param [utpEnabled] true means allow UTP
 * */
suspend fun RpcClient.setSessionData(
    altSpeedDown: Speed? = null,
    altSpeedEnabled: Boolean? = null,
    altSpeedTimeBegin: LocalTime? = null,
    altSpeedTimeDay: TrSpeedSchedule? = null,
    altSpeedTimeEnabled: Boolean? = null,
    altSpeedTimeEnd: LocalTime? = null,
    altSpeedUp: Speed? = null,
    blocklistEnabled: Boolean? = null,
    blocklistUrl: String? = null,
    cacheSizeMb: Int? = null,
    defaultTrackers: String? = null,
    dhtEnabled: Boolean? = null,
    downloadDir: String? = null,
    downloadQueueEnabled: Boolean? = null,
    downloadQueueSize: Int? = null,
    encryption: SessionEncryption? = null,
    idleSeedingLimitEnabled: Boolean? = null,
    idleSeedingLimit: Int? = null,
    incompleteDirEnabled: Boolean? = null,
    incompleteDir: String? = null,
    lpdEnabled: Boolean? = null,
    peerLimitGlobal: Int? = null,
    peerLimitPerTorrent: Int? = null,
    peerPortRandomOnStart: Boolean? = null,
    peerPort: Int? = null,
    pexEnabled: Boolean? = null,
    portForwardingEnabled: Boolean? = null,
    queueStalledEnabled: Boolean? = null,
    queueStalledMinutes: Int? = null,
    renamePartialFiles: Boolean? = null,
    reqq: Int? = null,
    scriptTorrentAddedEnabled: Boolean? = null,
    scriptTorrentAddedFilename: String? = null,
    scriptTorrentDoneEnabled: Boolean? = null,
    scriptTorrentDoneFilename: String? = null,
    scriptTorrentDoneSeedingEnabled: Boolean? = null,
    scriptTorrentDoneSeedingFilename: String? = null,
    seedQueueEnabled: Boolean? = null,
    seedQueueSize: Int? = null,
    seedRatioLimit: Double? = null,
    seedRatioLimited: Boolean? = null,
    sequentialDownload: Boolean? = null,
    speedLimitDownEnabled: Boolean? = null,
    speedLimitDown: Speed? = null,
    speedLimitUpEnabled: Boolean? = null,
    speedLimitUp: Speed? = null,
    startAddedTorrents: Boolean? = null,
    trashOriginalTorrentFiles: Boolean? = null,
    utpEnabled: Boolean? = null,
) {
    val sessionAccessorData = SessionAccessorData(
        altSpeedDown = Optional(altSpeedDown),
        altSpeedEnabled = Optional(altSpeedEnabled),
        altSpeedTimeBegin = Optional(altSpeedTimeBegin),
        altSpeedTimeDay = Optional(altSpeedTimeDay),
        altSpeedTimeEnabled = Optional(altSpeedTimeEnabled),
        altSpeedTimeEnd = Optional(altSpeedTimeEnd),
        altSpeedUp = Optional(altSpeedUp),
        blocklistEnabled = Optional(blocklistEnabled),
        blocklistUrl = Optional(blocklistUrl),
        cacheSizeMb = Optional(cacheSizeMb),
        defaultTrackers = Optional(defaultTrackers),
        dhtEnabled = Optional(dhtEnabled),
        downloadDir = Optional(downloadDir),
        downloadQueueEnabled = Optional(downloadQueueEnabled),
        downloadQueueSize = Optional(downloadQueueSize),
        encryption = Optional(encryption),
        idleSeedingLimitEnabled = Optional(idleSeedingLimitEnabled),
        idleSeedingLimit = Optional(idleSeedingLimit),
        incompleteDirEnabled = Optional(incompleteDirEnabled),
        incompleteDir = Optional(incompleteDir),
        lpdEnabled = Optional(lpdEnabled),
        peerLimitGlobal = Optional(peerLimitGlobal),
        peerLimitPerTorrent = Optional(peerLimitPerTorrent),
        peerPortRandomOnStart = Optional(peerPortRandomOnStart),
        peerPort = Optional(peerPort),
        pexEnabled = Optional(pexEnabled),
        portForwardingEnabled = Optional(portForwardingEnabled),
        queueStalledEnabled = Optional(queueStalledEnabled),
        queueStalledMinutes = Optional(queueStalledMinutes),
        renamePartialFiles = Optional(renamePartialFiles),
        reqq = Optional(reqq),
        scriptTorrentAddedEnabled = Optional(scriptTorrentAddedEnabled),
        scriptTorrentAddedFilename = Optional(scriptTorrentAddedFilename),
        scriptTorrentDoneEnabled = Optional(scriptTorrentDoneEnabled),
        scriptTorrentDoneFilename = Optional(scriptTorrentDoneFilename),
        scriptTorrentDoneSeedingEnabled = Optional(scriptTorrentDoneSeedingEnabled),
        scriptTorrentDoneSeedingFilename = Optional(scriptTorrentDoneSeedingFilename),
        seedQueueEnabled = Optional(seedQueueEnabled),
        seedQueueSize = Optional(seedQueueSize),
        seedRatioLimit = Optional(seedRatioLimit),
        seedRatioLimited = Optional(seedRatioLimited),
        sequentialDownload = Optional(sequentialDownload),
        speedLimitDownEnabled = Optional(speedLimitDownEnabled),
        speedLimitDown = Optional(speedLimitDown),
        speedLimitUpEnabled = Optional(speedLimitUpEnabled),
        speedLimitUp = Optional(speedLimitUp),
        startAddedTorrents = Optional(startAddedTorrents),
        trashOriginalTorrentFiles = Optional(trashOriginalTorrentFiles),
        utpEnabled = Optional(utpEnabled),
    )
    request(sessionAccessorData)
}