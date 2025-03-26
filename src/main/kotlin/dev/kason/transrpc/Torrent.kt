package dev.kason.transrpc

class Torrent {

    enum class Status {
        Stopped,
        QueuedVerify,
        Verifying,
        QueuedDownload,
        Downloading,
        QueuedSeed,
        Seeding
    }

    val status: Status = Status.Stopped

}