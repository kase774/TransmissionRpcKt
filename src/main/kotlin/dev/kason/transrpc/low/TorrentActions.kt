package dev.kason.transrpc.low

import dev.kason.transrpc.data.TorrentIds
import dev.kason.transrpc.transientDefaultValueError
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

// this file implements 3.1
internal enum class TorrentActionMethod(val methodName: String) {
    Start("torrent-start"),
    StartNow("torrent-start-now"),
    Stop("torrent-stop"),
    Verify("torrent-verify"),
    Reannounce("torrent-reannounce")
}

@Serializable
internal data class TorrentActionRequest(
    @Transient
    val actionMethod: TorrentActionMethod = transientDefaultValueError(),
    val ids: TorrentIds = TorrentIds.All
) : RpcRequest<NullResponse>() {
    override val method: String
        get() = actionMethod.methodName
}

/**
 * send a request to start the given torrent process
 *
* There are independent queues for seeding (`TR_UP`) and leeching (`TR_DOWN`).
*
* If the session already has enough non-stalled seeds/leeches when
* `startTorrent()` is called, the torrent will be moved into the
* appropriate queue and its state will be `Status.Queued{Seed, Download}`.
*
* To bypass the queue and unconditionally start the torrent use
* `startTorrentIgnoreQueue()`.
 * */
suspend fun RpcClient.startTorrent(ids: TorrentIds) {
    request(TorrentActionRequest(TorrentActionMethod.Start, ids))
}

/**
 *
 * Like [startTorrent], but resumes right away regardless of the queues.
 *
 * [source](https://github.com/transmission/transmission/blob/main/libtransmission/transmission.h#L589)
 * */
suspend fun RpcClient.startTorrentIgnoreQueue(ids: TorrentIds) {
    request(TorrentActionRequest(TorrentActionMethod.StartNow, ids))
}

/** send a request to stop the given torrents */
suspend fun RpcClient.stopTorrent(ids: TorrentIds) {
    request(TorrentActionRequest(TorrentActionMethod.Stop, ids))
}

/** verify the torrents.
 *
 * life pro tip: if you happen to
 * accidentally run verify torrent on all your existing torrents (which
 * may take a while - don't let the initial progress deceive you since it
 * starts with the smallest torrents first) you can stop the verification
 * process by closing the transmission client & daemon (make sure you actually
 * exit the daemon and not just the gui). after you start it again
 * you should no longer have to verify terabytes of data! */
suspend fun RpcClient.verifyTorrent(ids: TorrentIds) {
    request(TorrentActionRequest(TorrentActionMethod.Verify, ids))
}

/** re-announce to trackers now */
suspend fun RpcClient.reannounceTorrent(ids: TorrentIds) {
    request(TorrentActionRequest(TorrentActionMethod.Reannounce, ids))
}
