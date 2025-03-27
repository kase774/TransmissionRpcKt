package dev.kason.transrpc.test

import dev.kason.transrpc.data.TorrentIds
import dev.kason.transrpc.low.RpcClient
import dev.kason.transrpc.low.TorrentFields
import dev.kason.transrpc.low.getTorrentData


suspend fun main() {
    val rpc = RpcClient()
    rpc.getTorrentData(TorrentIds.All, listOf(TorrentFields.ErrorString)).map { it.debugString() }
        .forEach(::println)
}