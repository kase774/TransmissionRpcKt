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
    val text = """cookies 	string 	pointer to a string of one or more cookies.
download-dir 	string 	path to download the torrent to
filename 	string 	filename or URL of the .torrent file
labels 	array 	array of string labels
metainfo 	string 	base64-encoded .torrent content
paused 	boolean 	if true, don't start the torrent
peer-limit 	number 	maximum number of peers
bandwidthPriority 	number 	torrent's bandwidth tr_priority_t
files-wanted 	array 	indices of file(s) to download
files-unwanted 	array 	indices of file(s) to not download
priority-high 	array 	indices of high-priority file(s)
priority-low 	array 	indices of low-priority file(s)
priority-normal 	array 	indices of normal-priority file(s)
sequential_download 	boolean 	download torrent pieces sequentially"""
    for (line in text.lines()) {
        val (a, b, c) = line.split('\t').map { it.trim() }
        genParam(a, b, c)
    }
}

fun generateMutator(a: String, b: String, c: String) {
    println("/** $c */")
    val camelCaseName = camelCase(a)
    if (camelCaseName != a) {
        println("@SerialName(\"$a\")")
    }
    println("val $camelCaseName: Optional<${getType(a, b)}> = Optional.None(),")
}

fun genParam(a: String, b: String, c: String) {
    println("/** $c */")
    val camelCaseName = camelCase(a)
    println("$camelCaseName: ${getType(a, b)}? = null,")
}

fun getType(keyName: String, valueType: String): String {
    if ("date" in keyName.lowercase()) {
        return "Instant"
    }
    val actualName = valueType.substringBefore("(see below)")
        .trim()
    return mapTypes[actualName] ?: "List<String>"
}

/**
 *
 * transmission struct: $source */
fun generateObj(rawName: String, type: String, source: String) {
    if (source != "n/a") {
        println(
            "/**\n" +
                    " * \n" +
                    " * transmission struct: $source */"
        )
    } else {
        println("/**  */")
    }
    val realName = rawName.substringBefore("(")
    val camelCaseName = camelCase(realName)

//    data object ActivityDate: TorrentFields<Instant>("activityDate", Instant::class) {
//        override fun getValue(torrentAccessorData: TorrentAccessorData): Instant? =
//            torrentAccessorData.activityDate
//    }
    val typeStr = getType(realName, type)
    val capitalized =
        camelCaseName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    println(
        """data object ${capitalized}: TorrentFields<$typeStr>("$realName") {
        |   override fun getValue(torrentAccessorData: TorrentAccessorData): $typeStr? = 
        |       torrentAccessorData.$camelCaseName.value
        |   }
        |   
    """.trimMargin()
    )
}

fun generateProperty(rawName: String, type: String) {
    val camelCaseName = camelCase(rawName)
    var typeStr = getType(rawName, type)
    val capitalized =
        camelCaseName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    println("/** property corresponding to [TorrentFields.${capitalized}] */")
    if (camelCaseName != rawName) {
        println("@SerialName(\"$rawName\")")
    }
    if (typeStr == "Instant") {
        typeStr = "@Serializable(with = UnixTimeSerializer::class) Instant"
    }
    println("val $camelCaseName: Optional<$typeStr> = Optional.None(),")
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