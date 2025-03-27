@file:Suppress("MemberVisibilityCanBePrivate")

package dev.kason.transrpc.data

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

/** an id that represents a torrent (an item in a list torrent ids, based on id explanation in 3.1)
 * either a SHA1 hash string or a transmission int id */
@Serializable(with = TorrentId.Serializer::class)
sealed interface TorrentId {
    /** represents a sha1 hash torrent id, stable between transmission daemon restarts */
    @JvmInline
    @Serializable
    value class ShaHash(val hash: String) : TorrentId {
        init {
            require(hash.length == 40) { "sha1 hash should be 40 chars long (no prefix or anything): $hash" }
            for ((i, char) in hash.withIndex()) {
                require(char in '0'..'9' || char in 'a'..'f' || char in 'A'..'F') {
                    "illegal hex char $char in hash str $hash (index $i)"
                }
            }
        }
    }

    /** represents an int torrent id, NOT stable between transmission daemon restarts. */
    @JvmInline
    @Serializable
    value class SessionId(val id: UInt) : TorrentId

    object Serializer : JsonContentPolymorphicSerializer<TorrentId>(TorrentId::class) {
        // we don't want the default type discriminator
        override fun selectDeserializer(element: JsonElement): DeserializationStrategy<TorrentId> {
            val primitive = element as? JsonPrimitive ?: error("illegal element when deserializing to id: $element")
            if (primitive.isString) return ShaHash.serializer()
            // let it throw an error on bool (shouldn't happen)
            return SessionId.serializer()
        }
    }
}

/**
 * represents a possible `id` field, typically a group of torrents to apply an operation to
 *
 * All torrents are used if the `ids` argument is omitted. (All)
 *
 * `ids` should be one of the following:
 *
 *  - an integer referring to a torrent id (we ignore, just use IdList(num))
 *  - a list of torrent id numbers, SHA1 hash strings, or both (IdList)
 *  - a string, `recently-active`, for recently-active torrents (RecentlyActive)
 * */
@Serializable(with = TorrentIds.Serializer::class)
sealed interface TorrentIds {

    /** represents a list of torrent id numbers, SHA1 hash strings, or both (list of TorrentId) */
    @Serializable
    @JvmInline
    value class IdList(private val items: List<TorrentId>) : TorrentIds, List<TorrentId> by items {
        constructor(vararg ids: TorrentId) : this(ids.toList())
        constructor(vararg ids: String) : this(ids.map { TorrentId.ShaHash(it) })
        constructor(vararg ids: Int) : this(ids.map { TorrentId.SessionId(it.toUInt()) })
    }

    /** a [TorrentIds] that applies to all torrents that were recently active (pls define)  */
    @Serializable(with = RecentlyAdded.Serializer::class)
    data object RecentlyAdded : TorrentIds {
        // custom serializer to serialize recently added as a "recently-added" string
        object Serializer : KSerializer<RecentlyAdded> {
            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("TransmissionRpc.RecentlyAdded", PrimitiveKind.STRING)

            override fun serialize(encoder: Encoder, value: RecentlyAdded) =
                encoder.encodeString("recently-added")

            override fun deserialize(decoder: Decoder): RecentlyAdded = RecentlyAdded
        }
    }

    /** a [TorrentIds] that applies to all torrents. should be the default for all request classes */
    @Serializable(with = All.Serializer::class)
    data object All : TorrentIds {
        object Serializer : KSerializer<All> {
            // top 10 serializers of all time; any operation -> error
            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("TransmissionRpc.All", PrimitiveKind.STRING)

            override fun deserialize(decoder: Decoder): All =
                throw UnsupportedOperationException("deserialization unsupported")

            override fun serialize(encoder: Encoder, value: All) = error(
                "no serialization of TorrentIds.All allowed; if you see this error message it means that some " +
                        "property didn't define TorrentIds.All as its default or encodeDefaults was turned on " +
                        "for the json config."
            )
        }
    }

    object Serializer : JsonContentPolymorphicSerializer<TorrentIds>(TorrentIds::class) {
        override fun selectDeserializer(element: JsonElement): DeserializationStrategy<TorrentIds> {
            if (element is JsonArray) return IdList.serializer()
            if (element.jsonPrimitive.content == "recently-added") {
                return RecentlyAdded.serializer()
            }
            error("invalid id list when deserializing: $element, should be either string `recently-added` or a list of ids")
        }
    }
}