package dev.kason.transrpc.low

import dev.kason.transrpc.data.ByteCount
import dev.kason.transrpc.data.TorrentIds
import dev.kason.transrpc.transientDefaultValueError
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.io.File

// this file implements 4.3 to 4.7
// 4.3
@Serializable
data class BlocklistResponse(
    @SerialName("blocklist-size")
    val blockListSize: Int
) : RpcResponse

@Serializable
internal data object BlocklistUpdateRequest : RpcRequest<BlocklistResponse>() {
    override val method: String
        get() = "blocklist-update"
}

/** Fetches the blocklist from the blocklist url and returns
 * the size of the newly added blocklist. This may take a while. After updating
 * the blocklist, you can also easily get the size using [SessionFields.BlocklistSize]
 *
 * You SHOULD call this method after setting [SessionFields.BlocklistUrl]; I don't think
 * transmission automatically updates the blocklist if you just mutate it. */
suspend fun RpcClient.updateBlocklist(): BlocklistResponse =
    request(BlocklistUpdateRequest)

// 4.4

@Serializable
enum class IpProtocol {
    @SerialName("ipv4")
    IPv4,

    @SerialName("ipv6")
    IPv6,
}

@Serializable
data class PortCheckingRequest(
    @SerialName("ip_protocol")
    val ipProtocol: IpProtocol? = null
) : RpcRequest<PortCheckingResponse>() {
    override val method: String
        get() = "port-test"
}

@Serializable
data class PortCheckingResponse(
    /** true if port is open, false if port is closed */
    @SerialName("port-is-open")
    val portIsOpen: Boolean,
    /** ipv4 if the test was carried out on IPv4,
     * ipv6 if the test was carried out on IPv6, unset if it cannot be determined  */
    @SerialName("ip_protocol")
    val ipProtocol: IpProtocol? = null
) : RpcResponse

/**
 *  ip_protocol is a string specifying the IP protocol version to be used for the port test.
 *  Set to ipv4 to check IPv4, or set to ipv6 to check IPv6. For backwards compatibility,
 *  it is allowed to omit this argument (set to `null`) to get the behaviour before Transmission 4.1.0,
 *  which is to check whichever IP protocol the OS happened to use to connect to our port
 *  test service, frankly not very useful.*/
suspend fun RpcClient.portCheck(ipProtocol: IpProtocol?): PortCheckingResponse =
    request(PortCheckingRequest(ipProtocol))

// 4.5

@Serializable
internal data object ShutdownRequest : RpcRequest<NullResponse>() {
    override val method: String
        get() = "session-close"
}

suspend fun RpcClient.shutdown() {
    request(ShutdownRequest)
}

// 4.6
enum class QueueMethodMovement(val key: String) {
    /** Moves these torrents to the top of the queue */
    MoveTop("move-top"),

    /** Moves these torrents one step ahead in their queues */
    MoveUp("move-up"),

    /** Moves these torrents back one step in their queues */
    MoveDown("move-down"),

    /** Moves these torrents to the bottom of the queue */
    MoveBottom("move-bottom"),
}

@Serializable
internal data class QueueMovementRequest(
    @Transient
    val actionMethod: QueueMethodMovement = transientDefaultValueError(),
    val ids: TorrentIds = TorrentIds.All
) : RpcRequest<NullResponse>() {
    override val method: String
        get() = actionMethod.key
}

/** Modifies the queue according to the given [queueMovementRequests] */
suspend fun RpcClient.modifyQueue(queueMovementRequests: QueueMethodMovement, ids: TorrentIds) {
    request(QueueMovementRequest(queueMovementRequests, ids))
}

/** Moves these torrents to the top of the queue */
suspend fun RpcClient.moveToTopOfQueue(ids: TorrentIds) =
    modifyQueue(QueueMethodMovement.MoveTop, ids)

/** Moves these torrents one step ahead in their queues */
suspend fun RpcClient.moveUpInQueue(ids: TorrentIds) =
    modifyQueue(QueueMethodMovement.MoveUp, ids)

/** Moves these torrents back one step in their queues */
suspend fun RpcClient.moveDownInQueue(ids: TorrentIds) =
    modifyQueue(QueueMethodMovement.MoveDown, ids)

/** Moves these torrents to the bottom of the queue */
suspend fun RpcClient.moveToBottomOfQueue(ids: TorrentIds) =
    modifyQueue(QueueMethodMovement.MoveBottom, ids)

// 4.7

@Serializable
data class FreeSpaceRequest(
    val path: String
) : RpcRequest<FreeSpaceResponse>() {
    override val method: String
        get() = "free-space"
}

@Serializable
data class FreeSpaceResponse(
    val path: String,
    /** the size of the free space in that directory */
    @SerialName("size-bytes")
    val sizeBytes: ByteCount,
    /** the total capacity of that directory */
    @SerialName("total_size")
    val totalSize: ByteCount
) : RpcResponse

/** This method tests how much free space is available in a client-specified folder.
 * Path is the directory we are checking */
suspend fun RpcClient.calculateFreeSpace(path: String) {
    request(FreeSpaceRequest(path))
}

/** [calculateFreeSpace] but for a file. */
suspend fun RpcClient.calculateFreeSpace(file: File) =
    calculateFreeSpace(file.absolutePath)

