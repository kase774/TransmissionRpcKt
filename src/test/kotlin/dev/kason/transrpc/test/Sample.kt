package dev.kason.transrpc.test

import dev.kason.transrpc.data.TorrentId
import dev.kason.transrpc.data.TorrentIds
import dev.kason.transrpc.low.*
import java.io.File


suspend fun main() {
    val rpc = RpcClient()
    rpc.getTorrentData(TorrentIds.RecentlyActive, TorrentFields.UploadLimit)
}
