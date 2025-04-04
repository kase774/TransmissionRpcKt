package dev.kason.transrpc.test

import dev.kason.transrpc.low.RpcClient
import dev.kason.transrpc.low.getGroup


suspend fun main() {
    val rpc = RpcClient()
//    rpc.setBandwidthGroup(
//        BandwidthGroup(
//            honorsSessionLimits = true,
//            name = "sample-name",
//            speedLimitDown = 5.gigaBytesPerSecond,
//            speedLimitDownEnabled = false,
//            speedLimitUp = 5.gigaBytesPerSecond,
//            speedLimitUpEnabled = false
//        )
//    )

}
