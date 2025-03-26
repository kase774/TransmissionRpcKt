package dev.kason.transrpc

// returns whether `this` is an object (kotlin object - aka singleton)
internal inline fun <reified T : Any> T.isObject(): Boolean {
    return this.javaClass.kotlin.objectInstance != null
}

fun transientDefaultValueError(): Nothing =
    error("failed to init property for serialize-only transient property (went to error default value)")