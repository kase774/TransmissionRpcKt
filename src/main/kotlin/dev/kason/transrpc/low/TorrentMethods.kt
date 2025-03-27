package dev.kason.transrpc.low

import dev.kason.transrpc.data.Optional
import dev.kason.transrpc.data.Priority
import dev.kason.transrpc.data.TorrentId
import dev.kason.transrpc.data.TorrentIds
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.io.File

// this file implements 3.4-3.7

// 3.4, adding
@Serializable
internal data class TorrentAddRequest(
    val cookies: Optional<String> = Optional.None(),
    @SerialName("download-dir")
    val downloadDir: Optional<String> = Optional.None(),
    val filename: Optional<String> = Optional.None(),
    val labels: Optional<List<Int>> = Optional.None(),
    val metainfo: Optional<String> = Optional.None(),
    val paused: Optional<Boolean> = Optional.None(),
    @SerialName("peer-limit")
    val peerLimit: Optional<Int> = Optional.None(),
    val bandwidthPriority: Optional<Priority> = Optional.None(),
    @SerialName("files-wanted")
    val filesWanted: Optional<List<Int>> = Optional.None(),
    @SerialName("files-unwanted")
    val filesUnwanted: Optional<List<Int>> = Optional.None(),
    @SerialName("priority-high")
    val priorityHigh: Optional<List<Int>> = Optional.None(),
    @SerialName("priority-low")
    val priorityLow: Optional<List<Int>> = Optional.None(),
    @SerialName("priority-normal")
    val priorityNormal: Optional<List<Int>> = Optional.None(),
    @SerialName("sequential_download")
    val sequentialDownload: Optional<Boolean> = Optional.None()
) : RpcRequest<TorrentAddedResult>() {
    override val method: String
        get() = "torrent-add"
}

@Serializable(with = TorrentAddedResult.Serializer::class)
data class TorrentAddedResult(
    val id: TorrentId.SessionId,
    val name: String,
    val hashString: TorrentId.ShaHash,
    val isDuplicate: Boolean
) : RpcResponse {
    internal object Serializer : KSerializer<TorrentAddedResult> {
        @Serializable
        internal data class Surrogate(
            @SerialName("torrent-added")
            val torrentAdded: Result? = null,
            @SerialName("torrent-duplicate")
            val torrentDuplicate: Result? = null,
        ) {
            @Serializable
            internal data class Result(
                val id: TorrentId.SessionId,
                val name: String,
                val hashString: TorrentId.ShaHash,
            )
        }

        override val descriptor: SerialDescriptor =
            SerialDescriptor("TransmissionRpc.TorrentAddedResult", Surrogate.serializer().descriptor)

        override fun deserialize(decoder: Decoder): TorrentAddedResult {
            val surrogate = decoder.decodeSerializableValue(Surrogate.serializer())
            val isDuplicate: Boolean
            val result: Surrogate.Result = if ((surrogate.torrentAdded != null).also { isDuplicate = !it }) {
                surrogate.torrentAdded!!
            } else surrogate.torrentDuplicate!!
            return TorrentAddedResult(
                result.id,
                result.name,
                result.hashString,
                isDuplicate
            )
        }

        override fun serialize(encoder: Encoder, value: TorrentAddedResult) =
            throw UnsupportedOperationException("should not serialize a torrent added result...")
    }
}

/**
 * The format of the cookies should be NAME=CONTENTS, where NAME is the cookie name and CONTENTS
 * is what the cookie should contain. Set multiple cookies like this: name1=content1; name2=content2;
 * etc. See libcurl documentation for more information.
 * */
internal fun formatCookies(cookies: Map<String, String>) =
    cookies.entries.joinToString(separator = "; ", postfix = ";") {
        "${it.key}=${it.value}"
    }

/** add a torrent from a torrent file */
suspend fun RpcClient.addTorrentFromFile(
    /** filename of the .torrent file */
    file: File,
    /** pointer to a string of one or more cookies. */
    cookies: Map<String, String>? = null,
    /** path to download the torrent to */
    downloadDir: File? = null,
    /** array of string labels */
    labels: List<Int>? = null,
    /** if true, don't start the torrent */
    paused: Boolean? = null,
    /** maximum number of peers */
    peerLimit: Int? = null,
    /** torrent's bandwidth tr_priority_t */
    bandwidthPriority: Priority? = null,
    /** indices of file(s) to download */
    filesWanted: List<Int>? = null,
    /** indices of file(s) to not download */
    filesUnwanted: List<Int>? = null,
    /** indices of high-priority file(s) */
    priorityHigh: List<Int>? = null,
    /** indices of low-priority file(s) */
    priorityLow: List<Int>? = null,
    /** indices of normal-priority file(s) */
    priorityNormal: List<Int>? = null,
    /** download torrent pieces sequentially */
    sequentialDownload: Boolean? = null
): TorrentAddedResult {
    require(file.exists()) { "file does not exist" }
    return addTorrentFromUrl(
        url = file.absolutePath,
        cookies,
        downloadDir,
        labels,
        paused,
        peerLimit,
        bandwidthPriority,
        filesWanted,
        filesUnwanted,
        priorityHigh,
        priorityLow,
        priorityNormal,
        sequentialDownload,
    )
}

/** add a torrent from a torrent file */
suspend fun RpcClient.addTorrentFromUrl(
    /** URL of the .torrent file */
    url: String,
    /** pointer to a string of one or more cookies. */
    cookies: Map<String, String>? = null,
    /** path to download the torrent to */
    downloadDir: File? = null,
    /** array of string labels */
    labels: List<Int>? = null,
    /** if true, don't start the torrent */
    paused: Boolean? = null,
    /** maximum number of peers */
    peerLimit: Int? = null,
    /** torrent's bandwidth tr_priority_t */
    bandwidthPriority: Priority? = null,
    /** indices of file(s) to download */
    filesWanted: List<Int>? = null,
    /** indices of file(s) to not download */
    filesUnwanted: List<Int>? = null,
    /** indices of high-priority file(s) */
    priorityHigh: List<Int>? = null,
    /** indices of low-priority file(s) */
    priorityLow: List<Int>? = null,
    /** indices of normal-priority file(s) */
    priorityNormal: List<Int>? = null,
    /** download torrent pieces sequentially */
    sequentialDownload: Boolean? = null
) = request(
    TorrentAddRequest(
        Optional(cookies?.let { formatCookies(it) }),
        Optional(downloadDir?.name),
        Optional(url),
        Optional(labels),
        Optional(null),
        Optional(paused),
        Optional(peerLimit),
        Optional(bandwidthPriority),
        Optional(filesWanted),
        Optional(filesUnwanted),
        Optional(priorityHigh),
        Optional(priorityLow),
        Optional(priorityNormal),
        Optional(sequentialDownload)
    )
)

private const val VALID_BASE64_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/="

/** add a torrent from a torrent file */
suspend fun RpcClient.addTorrentFromBase64EncodedContent(
    /** base64-encoded .torrent content */
    metainfo: String,
    /** pointer to a string of one or more cookies. */
    cookies: Map<String, String>? = null,
    /** path to download the torrent to */
    downloadDir: File? = null,
    /** array of string labels */
    labels: List<Int>? = null,
    /** if true, don't start the torrent */
    paused: Boolean? = null,
    /** maximum number of peers */
    peerLimit: Int? = null,
    /** torrent's bandwidth tr_priority_t */
    bandwidthPriority: Priority? = null,
    /** indices of file(s) to download */
    filesWanted: List<Int>? = null,
    /** indices of file(s) to not download */
    filesUnwanted: List<Int>? = null,
    /** indices of high-priority file(s) */
    priorityHigh: List<Int>? = null,
    /** indices of low-priority file(s) */
    priorityLow: List<Int>? = null,
    /** indices of normal-priority file(s) */
    priorityNormal: List<Int>? = null,
    /** download torrent pieces sequentially */
    sequentialDownload: Boolean? = null
): TorrentAddedResult {
    require(metainfo.all { it in VALID_BASE64_CHARS }) { "invalid base64 metainfo string!" }
    return request(
        TorrentAddRequest(
            Optional(cookies?.let { formatCookies(it) }),
            Optional(downloadDir?.name),
            Optional(null),
            Optional(labels),
            Optional(metainfo),
            Optional(paused),
            Optional(peerLimit),
            Optional(bandwidthPriority),
            Optional(filesWanted),
            Optional(filesUnwanted),
            Optional(priorityHigh),
            Optional(priorityLow),
            Optional(priorityNormal),
            Optional(sequentialDownload)
        )
    )
}

// 3.5 removing
@Serializable
internal data class TorrentRemoveRequest(
    val ids: TorrentIds,
    @SerialName("delete-local-data")
    val deleteLocalData: Boolean
) : RpcRequest<NullResponse>() {
    override val method: String
        get() = "torrent-remove"
}

/** Removes the torrents with the given id. [deleteLocalData] describes whether
 * to delete the local data */
suspend fun RpcClient.removeTorrents(ids: TorrentIds, deleteLocalData: Boolean = false) {
    request(TorrentRemoveRequest(ids, deleteLocalData))
}

/** Removes the torrent with the given id. [deleteLocalData] describes whether
 * to delete the local data */
suspend fun RpcClient.removeTorrent(ids: TorrentId, deleteLocalData: Boolean = false) =
    removeTorrents(TorrentIds.IdList(ids), deleteLocalData)

// 3.6 moving

@Serializable
internal data class TorrentMoveRequest(
    val ids: TorrentIds,
    val location: String,
    val move: Boolean
) : RpcRequest<NullResponse>() {
    override val method: String
        get() = "torrent-set-location"
}

/** Moves torrents with the given id to the new location specified by [location].
 *
 *  move: if true, move from previous location. otherwise, search "location" for files (default: false) */
suspend fun RpcClient.moveTorrent(ids: TorrentIds, location: File, move: Boolean = false) =
    moveTorrent(ids, location.absolutePath, move)


/** Moves torrents with the given id to the new location specified by [location].
 *
 *  move: if true, move from previous location. otherwise, search "location" for files (default: false) */
suspend fun RpcClient.moveTorrent(ids: TorrentIds, location: String, move: Boolean = false) {
    request(TorrentMoveRequest(ids, location, move))
}

// 3.7 renaming torrent path

@Serializable
internal data class TorrentRenameRequest(
    val ids: TorrentIds,
    val path: String,
    val name: String
) : RpcRequest<NullResponse>() {
    override val method: String
        get() = "torrent-rename-path"
}

/**
 * Renames the file or directory specified by [path] to a new name, [name]. This function
 * can't change the directory structure of the torrent files - it can only rename folders
 * or files within it.
 *
 * Renaming the root folder of the torrent will also change the torrent name.
 * This function can only be applied to 1 torrent at a time.
 *
 * As an example, say we have a torrent with structure:
 * ```
 *  /frobnitz-linux
 *     - checksum
 *     - frobnitz.iso
 * ```
 *
 * so we have 2 total files:
 *  - `/frobnitz-linux/checksum`
 *  - `/frobnitz-linux/frobnitz.iso`
 *
 * Running:
 *
 * ```kt
 * client.renameTorrent($id, "frobnitz-linux", "foo")
 * ```
 * will rename the folder `frobnitz-linux` to `foo`: thus, our
 * directory structure becomes:
 * ```
 *  /foo
 *     - checksum
 *     - frobnitz.iso
 * ```
 *
 * Our files are now `/foo/checksum` and `/foo/frobnitz.iso`
 *
 * Another example; running:
 *
 * ```kt
 * client.renameTorrent($id, "frobnitz-linux/checksum", "foo")
 * ```
 *
 * since `/frobnitz-linux/checksum` is a singular file, we only rename the file
 * so the file name becomes `/frobnitz-linux/foo` (note that the new name `foo` is only
 * the name within the directory, not from torrent root)
 *
 * Our file structure looks like:
 * ```
 *  /frobnitz-linux
 *     - foo
 *     - frobnitz.iso
 * ```
 *
 * This function will return an error if [path] can't be found, [name] already exists
 * or has a directory separator, or is invalid.
 *
 * [Docs](https://github.com/transmission/transmission/blob/main/docs/rpc-spec.md#37-renaming-a-torrents-path)
 * [Transmission Source](https://github.com/transmission/transmission/blob/main/libtransmission/transmission.h#L898)
 * [Python impl](https://github.com/trim21/transmission-rpc/blob/8cd8c22353adf85795a62687044526c5537fa516/transmission_rpc/client.py#L761)
 * */
suspend fun RpcClient.renameTorrent(id: TorrentId, path: String, name: String) {
    request(TorrentRenameRequest(TorrentIds.IdList(id), path, name))
}