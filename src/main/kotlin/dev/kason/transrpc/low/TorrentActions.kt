package dev.kason.transrpc.low

import dev.kason.transrpc.data.TorrentIds
import dev.kason.transrpc.transientDefaultValueError
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

// this file implements 3.1
enum class TorrentActionMethod(val methodName: String) {
    Start("torrent-start"),
    StartNow("torrent-start-now"),
    Stop("torrent-stop"),
    Verify("torrent-verify"),
    Reannounce("torrent-reannounce")
}

@Serializable
data class TorrentActionRequest(
    @Transient
    val actionMethod: TorrentActionMethod = transientDefaultValueError(),
    val ids: TorrentIds = TorrentIds.All
) : RpcRequest<NullResponse>() {
    override val method: String
        get() = actionMethod.methodName
}

suspend fun RpcClient.startTorrent(ids: TorrentIds) =
    request(TorrentActionRequest(TorrentActionMethod.Start, ids))

suspend fun RpcClient.startTorrentIgnoreQueue(ids: TorrentIds) =
    request(TorrentActionRequest(TorrentActionMethod.StartNow, ids))

suspend fun RpcClient.stopTorrent(ids: TorrentIds) =
    request(TorrentActionRequest(TorrentActionMethod.Stop, ids))

suspend fun RpcClient.verifyTorrent(ids: TorrentIds) =
    request(TorrentActionRequest(TorrentActionMethod.Verify, ids))

suspend fun RpcClient.reannounceTorrent(ids: TorrentIds) =
    request(TorrentActionRequest(TorrentActionMethod.Reannounce, ids))
