package dev.kason.transrpc.low

import dev.kason.transrpc.data.ByteCount
import dev.kason.transrpc.data.DurationSerializer
import dev.kason.transrpc.data.Speed
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration

// this file implements 4.2
@Serializable
data class SessionSnapshot(
    val activeTorrentCount: Int,
    @Serializable(with = Speed.ByteSerializer::class)
    val downloadSpeed: Speed,
    val pausedTorrentCount: Int,
    val torrentCount: Int,
    @Serializable(with = Speed.ByteSerializer::class)
    val uploadSpeed: Speed,
    @SerialName("cumulative-stats")
    val cumulative: Stats,
    @SerialName("current-stats")
    val current: Stats
): RpcResponse {
    @Serializable
    data class Stats(
        val uploadedBytes: ByteCount,
        val downloadedBytes: ByteCount,
        val filesAdded: Int,
        val sessionCount: Int,
        @SerialName("secondsActive")
        @Serializable(with = DurationSerializer::class)
        val active: Duration
    )
}

@Serializable
internal data object SessionStatsRequest: RpcRequest<SessionSnapshot>() {
    override val method: String
        get() = "session-stats"
}

/** Retrieves a [SessionSnapshot] object that contains this session's stats data */
suspend fun RpcClient.getSessionStats(): SessionSnapshot =
    request(SessionStatsRequest)