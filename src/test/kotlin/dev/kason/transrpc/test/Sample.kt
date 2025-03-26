package dev.kason.transrpc.test

import dev.kason.transrpc.data.TorrentId
import dev.kason.transrpc.data.TorrentIds
import dev.kason.transrpc.data.TorrentRatioLimit
import dev.kason.transrpc.low.RpcClient
import dev.kason.transrpc.low.sendTorrentSetProperties
import dev.kason.transrpc.low.torrentSetProperties

suspend fun main() {
    val client = RpcClient(username = "admin", password = "*****")

    client.torrentSetProperties(
        TorrentIds.IdList(1),
        seedRatioMode = TorrentRatioLimit.Global
    )
}