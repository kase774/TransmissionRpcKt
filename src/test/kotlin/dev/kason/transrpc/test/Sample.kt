package dev.kason.transrpc.test

import dev.kason.transrpc.data.*
import dev.kason.transrpc.low.*


suspend fun main() {
    val rpc = RpcClient()

    println(rpc.getSessionStats())
}
