package dev.kason.transrpc.low

import dev.kason.transrpc.data.Speed
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


// this file implements 4.8,
// there seems to be no discussion of bandwidths outside of this rpc-spec chapter though;;
// can't find any mention of it on my transmission client gui either, but the results
// of the rpc calls match whats expected...
// not way to add a torrent to a bandwidth group either, weird.

// 4.8.1
@Serializable
data class BandwidthGroup(
    /** true if session upload limits are honored */
    val honorsSessionLimits: Boolean,
    val name: String,
    @SerialName("speed-limit-down-enabled")
    val speedLimitDownEnabled: Boolean,
    /** max global download speed */
    @SerialName("speed-limit-down")
    val speedLimitDown: Speed,
    @SerialName("speed-limit-up-enabled")
    val speedLimitUpEnabled: Boolean,
    /** max global upload speed */
    @SerialName("speed-limit-up")
    val speedLimitUp: Speed
) : RpcRequest<NullResponse>() {
    override val method: String
        get() = "group-set"
}

/** Sets the bandwidth group specified by the name property in [bandwidthGroup] to the
 * properties in bandwidth group. */
suspend fun RpcClient.setBandwidthGroup(bandwidthGroup: BandwidthGroup) {
    request(bandwidthGroup)
}


// 4.8.2
@Serializable
data class BandwidthGroupAccessorRequest(
    val group: List<String>? = null
) : RpcRequest<BandwidthGroupAccessorResponse>() {
    override val method: String
        get() = "group-get"
}

@Serializable
data class BandwidthGroupAccessorResponse(
    val group: List<BandwidthGroup>
) : RpcResponse

/** Returns the bandwidth group for the group, or null if it doesn't exist? */
// this just straight up returns all the groups i think
@Deprecated("don't use named groups for bandwidth", replaceWith = ReplaceWith("getAllGroups().first { it.name == group }"))
suspend fun RpcClient.getGroup(group: String): BandwidthGroup? =
    getGroups(listOf(group)).firstOrNull()


@Deprecated("don't use named groups for bandwidth", replaceWith = ReplaceWith("getAllGroups().filter { it.name in groups }"))
suspend fun RpcClient.getGroups(groups: List<String>): List<BandwidthGroup> {
    return request(BandwidthGroupAccessorRequest(groups)).group
}

suspend fun RpcClient.getAllGroups(): List<BandwidthGroup> =
    request(BandwidthGroupAccessorRequest()).group