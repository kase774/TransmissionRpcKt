package dev.kason.transrpc.test

import dev.kason.transrpc.data.Optional
import dev.kason.transrpc.data.TorrentIds
import dev.kason.transrpc.low.*


suspend fun main() {
    val rpc = RpcClient()
    println(rpc.setSessionData(
        SessionAccessorData(configDir = Optional.Some("sdfdf"))
    ))
    println(rpc.getSessionData())
}
