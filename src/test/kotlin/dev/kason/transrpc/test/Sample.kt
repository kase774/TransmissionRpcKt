package dev.kason.transrpc.test

import dev.kason.transrpc.data.TorrentId
import dev.kason.transrpc.data.TorrentIds
import dev.kason.transrpc.low.*


suspend fun main() {
    val rpc = RpcClient()
    // returns TorrentAccessorData with the hash string property filled out
    rpc.getTorrentData(TorrentIds.All, listOf(TorrentFields.Group))
        .map { it.debugString() }.forEach { println(it) }
    rpc.verifyTorrent(TorrentIds.All)
}
