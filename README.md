### Transmission Rpc for Kotlin
This library offers a way for Kotlin applications to interact with an instance of
[Transmission](https://transmissionbt.com/), a popular BitTorrent client, through the [Transmission RPC
API](https://github.com/transmission/transmission/blob/main/docs/rpc-spec.md).

Some of this code is based upon trim21's excellent Python wrapper over the Transmission API:
https://github.com/trim21/transmission-rpc

### Usage
This is still in development, so I haven't published it to Gradle yet. However, if you want to test it
on your machine, see `Contributions`. I do plan to publish this later though, when it is more stable.

By default, Transmission does NOT allow remote access via RPC! To enable, go to `Edit > Preferences > Remote`
and enable the `Allow Remote access` option. (if you are attempting to connect to another computer, 
make sure that your IP is on the whitelist as well!)
If you configure the other options, be sure to update your connection string.

#### Sample usage:

```kt
    // modify connection settings, such as username / password in Client
    // constructor
    val client = RpcClient(username = "admin", password = "*****")
    
    // updates the property of torrent with the given sha1 has to have
    // a global seed ratio mode
    client.torrentSetProperties(
        TorrentIds.IdList("a0b1c2d3e4f56789dcbaa0b1c2d3e4f56789dcba"),
        seedRatioMode = TorrentRatioLimit.Global
    )
```

This library relies on Ktor for client connections, so it's advised that you know
how to deal with coroutines.

### Goals

Chapters: (direct implementation of rpc protocol in `dev.kason.transrpc.low`)

 - [x] connection (2.1 - 2.3)
 - [ ] torrent requests (3)
   - [x] action (3.1)
   - [x] mutators (3.2)
   - [ ] accessors (3.3) [in progress]
   - [ ] methods (3.4-) 
     - [x] adding (3.4)
     - [x] removing (3.5)
     - [ ] moving (3.6)
     - [ ] renaming path (3.7)
 - [ ] session args (4.1)
   - [ ] mutators (4.1.1)
   - [ ] accessors (4.1.2)
 - [ ] session stats (4.2)
 - [ ] session methods (4.3-4.7)
   - [ ] block list (4.3)
   - [ ] port checking (4.4)
   - [ ] shutdown (4.5)
   - [ ] queue movement (4.6)
   - [ ] free space (4.7)
 - [ ] bandwidth groups (4.8)

In addition, a light but higher level wrapper around the `low` to 
make management easier:

 - [ ] torrent wrapper
 - [ ] configurable caching system (lazy state updates)
 - [ ] session wrapper

### Contributions

This project aims to support Transmission 4.0.0 to 4.1.0 (`rpc-version-semver` 5.3.0, `rpc-version`: 17),
the current `stable` branch. If you want to contribute, please consider adding me on Discord or Matrix: `croissant676`.

I recommend using IntelliJ idea to set help work on this if you want to contribute. If you clone the repository,
Gradle should set everything else up for you. 