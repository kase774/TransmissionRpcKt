package dev.kason.transrpc.test

import dev.kason.transrpc.data.TorrentIds
import dev.kason.transrpc.low.RpcClient
import dev.kason.transrpc.low.getGroup
import dev.kason.transrpc.low.moveTorrent
import dev.kason.transrpc.low.torrentSetProperties
import java.io.File


suspend fun main() {
    val rpc = RpcClient()
    rpc.moveTorrent(
        ids = TorrentIds.All,
        location = File("/mnt/aed32d39-47e0-47ed-a677-4d99da2408e2/Videos"),
        move = false
    )
}
