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
    val text = """alt-speed-down 	number 	max global download speed (KBps)
alt-speed-enabled 	boolean 	true means use the alt speeds
alt-speed-time-begin 	number 	when to turn on alt speeds (units: minutes after midnight)
alt-speed-time-day 	number 	what day(s) to turn on alt speeds (look at tr_sched_day)
alt-speed-time-enabled 	boolean 	true means the scheduled on/off times are used
alt-speed-time-end 	number 	when to turn off alt speeds (units: same)
alt-speed-up 	number 	max global upload speed (KBps)
blocklist-enabled 	boolean 	true means enabled
blocklist-size 	number 	number of rules in the blocklist
blocklist-url 	string 	location of the blocklist to use for blocklist-update
cache-size-mb 	number 	maximum size of the disk cache (MB)
config-dir 	string 	location of transmission's configuration directory
default-trackers 	string 	announce URLs, one per line, and a blank line between tiers.
dht-enabled 	boolean 	true means allow DHT in public torrents
download-dir 	string 	default path to download torrents
download-dir-free-space 	number 	DEPRECATED Use the free-space method instead.
download-queue-enabled 	boolean 	if true, limit how many torrents can be downloaded at once
download-queue-size 	number 	max number of torrents to download at once (see download-queue-enabled)
encryption 	string 	required, preferred, tolerated
idle-seeding-limit-enabled 	boolean 	true if the seeding inactivity limit is honored by default
idle-seeding-limit 	number 	torrents we're seeding will be stopped if they're idle for this long
incomplete-dir-enabled 	boolean 	true means keep torrents in incomplete-dir until done
incomplete-dir 	string 	path for incomplete torrents, when enabled
lpd-enabled 	boolean 	true means allow Local Peer Discovery in public torrents
peer-limit-global 	number 	maximum global number of peers
peer-limit-per-torrent 	number 	maximum global number of peers
peer-port-random-on-start 	boolean 	true means pick a random peer port on launch
peer-port 	number 	port number
pex-enabled 	boolean 	true means allow PEX in public torrents
port-forwarding-enabled 	boolean 	true means ask upstream router to forward the configured peer port to transmission using UPnP or NAT-PMP
queue-stalled-enabled 	boolean 	whether or not to consider idle torrents as stalled
queue-stalled-minutes 	number 	torrents that are idle for N minuets aren't counted toward seed-queue-size or download-queue-size
rename-partial-files 	boolean 	true means append .part to incomplete files
reqq 	number 	the number of outstanding block requests a peer is allowed to queue in the client
rpc-version-minimum 	number 	the minimum RPC API version supported
rpc-version-semver 	string 	the current RPC API version in a semver-compatible string
rpc-version 	number 	the current RPC API version
script-torrent-added-enabled 	boolean 	whether or not to call the added script
script-torrent-added-filename 	string 	filename of the script to run
script-torrent-done-enabled 	boolean 	whether or not to call the done script
script-torrent-done-filename 	string 	filename of the script to run
script-torrent-done-seeding-enabled 	boolean 	whether or not to call the seeding-done script
script-torrent-done-seeding-filename 	string 	filename of the script to run
seed-queue-enabled 	boolean 	if true, limit how many torrents can be uploaded at once
seed-queue-size 	number 	max number of torrents to uploaded at once (see seed-queue-enabled)
seedRatioLimit 	double 	the default seed ratio for torrents to use
seedRatioLimited 	boolean 	true if seedRatioLimit is honored by default
sequential_download 	boolean 	true means sequential download is enabled by default for added torrents
session-id 	string 	the current X-Transmission-Session-Id value
speed-limit-down-enabled 	boolean 	true means enabled
speed-limit-down 	number 	max global download speed (KBps)
speed-limit-up-enabled 	boolean 	true means enabled
speed-limit-up 	number 	max global upload speed (KBps)
start-added-torrents 	boolean 	true means added torrents will be started right away
trash-original-torrent-files 	boolean 	true means the .torrent file of added torrents will be deleted
units 	object 	see below
utp-enabled 	boolean 	true means allow UTP
version 	string 	long version string"""
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
    val camelCaseName = camelCase(a)
    println("@param [$camelCaseName] $c")
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

/**
 *
 * transmission struct: $source */
fun generateObjSession(rawName: String, type: String, docs: String) {
    if (docs != "n/a") {
        println(
            "/** $docs */"
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
        """    data object $capitalized: SessionFields<$typeStr>("$realName", true)
    """.trimMargin()
    )
}

fun genWhenStatementExtension(rawName: String, type: String) {
    val realName = rawName.substringBefore("(")
    val camelCaseName = camelCase(realName)
    val typeStr = getType(realName, type)
    val capitalized =
        camelCaseName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    println("is $capitalized -> ($camelCaseName.value) as $typeStr?")
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


fun generatePropertySession(rawName: String, type: String) {
    val camelCaseName = camelCase(rawName)
    var typeStr = getType(rawName, type)
    val capitalized =
        camelCaseName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    println("/** property corresponding to [SessionFields.${capitalized}] */")
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