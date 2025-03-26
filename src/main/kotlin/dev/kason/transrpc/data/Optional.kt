package dev.kason.transrpc.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/** represents a value that may or may not be there. [None] represents
 * the lack of a value, while [Some] indicates that some value exists (similar to null).
 * this wrapper class functions well with kotlinx serialization; note that all
 * properties with an optional type should have [Optional.None] as their default */
// based on kordlib's optional impl
@Serializable(with = Optional.Serializer::class)
sealed interface Optional<out T> {

    val value: T?

    @JvmInline
    @Serializable
    value class Some<T>(override val value: T) : Optional<T>
    @Serializable
    class None<T> private constructor() : Optional<T> {
        override val value: T?
            get() = null

        companion object {
            private val none = None<Nothing>()

            @Suppress("UNCHECKED_CAST")
            operator fun <T> invoke(): None<T> =
                none as None<T>
        }
    }

    class Serializer<T>(private val serializer: KSerializer<T>) : KSerializer<Optional<T>> {
        override val descriptor: SerialDescriptor = serializer.descriptor

        override fun deserialize(decoder: Decoder): Optional<T> =
            Some(serializer.deserialize(decoder))

        override fun serialize(encoder: Encoder, value: Optional<T>) = when (value) {
            is Some -> encoder.encodeSerializableValue(serializer, value.value)
            is None -> error("should not be encoding None unless some property does not have Optional.None set as default or" +
                    "Json has encodeDefaults set to true.")
        }
    }
}

fun <T> Optional(value: T?): Optional<T> =
    if (value == null) Optional.None() else Optional.Some(value)