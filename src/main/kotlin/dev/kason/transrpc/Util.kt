package dev.kason.transrpc

import io.ktor.util.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.BitSet

// returns whether `this` is an object (kotlin object - aka singleton)
internal inline fun <reified T : Any> T.isObject(): Boolean {
    return this.javaClass.kotlin.objectInstance != null
}

fun transientDefaultValueError(): Nothing =
    error("failed to init property for serialize-only transient property (went to error default value)")

// for the pieces property which is serialized as base64
internal object Base64ToBitsetSerializer: KSerializer<BitSet> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("TransmissionRpc.Base64Bitset", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): BitSet {
        val data = decoder.decodeString().decodeBase64Bytes()
        return BitSet.valueOf(data)
    }

    override fun serialize(encoder: Encoder, value: BitSet) {
        encoder.encodeString(value.toByteArray().encodeBase64())
    }
}