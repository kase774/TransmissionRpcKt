package dev.kason.transrpc.test

import java.util.*

val mapTypes = mapOf(
    "number" to "Int",
    "string" to "String",
    "boolean" to "Boolean",
    "double" to "Double",
    "float" to "Double",
    "array" to "List<Int>"
)

fun main() {
    // script to generate definitions from rpc docs
    val text = """"""
    for (line in text.lines()) {
        val (a, b, c) = line.split('\t').map {it.trim()}
        println("/** $c */")
        val camelCaseName = camelCase(a)
        if (camelCaseName != a) {
            println("@SerialName(\"$a\")")
        }
        println("val $camelCaseName: Optional<${mapTypes[b]}> = Optional.None(),")
        println("Optional($camelCaseName),")
    }
}

fun camelCase(possibleOther: String): String {
    if ("-" !in possibleOther && '_' !in possibleOther) {
        return possibleOther
    }
    val words = possibleOther.split('-', '_')
        .map { it.lowercase() }
    return buildString {
        append(words.first())
        for (word in words.drop(1)) {
            append(word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() })
        }
    }
}