# Transmission Rpc for Kotlin
This library offers a way for Kotlin applications to interact with an instance of
[Transmission](https://transmissionbt.com/), a popular BitTorrent client, through the [Transmission RPC
API](https://github.com/transmission/transmission/blob/main/docs/rpc-spec.md).

Some of this code is based upon trim21's excellent Python wrapper over the Transmission API:
https://github.com/trim21/transmission-rpc

Looking for co-maintainers!! (check out the contributions section :3)

This library was originally written for my own purposes, so parts of the library that I didn't need aren't
prioritized as much (for ex, don't plan on typing 4.1.1~4.1.2, session data, like I did 
for `TorrentAccessors.kt`). 

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

    // returns TorrentAccessorData with the hash string property filled out
    val data = client.getTorrentData(TorrentIds.All, listOf(TorrentFields.HashString))
        // get the hash strings of each torrent!
        .map { it.hashString }
```

This library uses `kotlinx.coroutines` for async connections and http management bc
Ktor. Read more about coroutines [here](https://kotlinlang.org/docs/coroutines-guide.html)!

### Goals

Chapters: (direct implementation of rpc protocol in `dev.kason.transrpc`)

 - [x] connection (2.1 - 2.3)
 - [x] torrent requests (3)
   - [x] action (3.1)
   - [x] mutators (3.2)
   - [x] accessors (3.3)
   - [x] methods (3.4-) 
     - [x] adding (3.4)
     - [x] removing (3.5)
     - [x] moving (3.6)
     - [x] renaming path (3.7)
 - [x] session args (4.1)
   - [x] mutators (4.1.1)
   - [x] accessors (4.1.2)
 - [ ] session stats (4.2)
 - [ ] session methods (4.3-4.7)
   - [ ] block list (4.3)
   - [ ] port checking (4.4)
   - [ ] shutdown (4.5)
   - [ ] queue movement (4.6)
   - [ ] free space (4.7)
 - [ ] bandwidth groups (4.8)

~~In addition, a light but higher level wrapper around the `low` to 
make management easier:~~

 - [ ] ~~torrent wrapper~~
 - [ ] ~~configurable caching system (lazy state updates)~~
 - [ ] ~~session wrapper~~

Another lightweight wrapper around light that makes it slightly easier to modify
(but not a full cache system). Had to scale back initial plans for the higher level wrapper since I 
need to work on other projects as well. 

### Libraries used
 - Kotlin logging wrapper around Slf4j, logback backend for logging
 - Ktor for http requests
 - Kotlinx serialization for serializing
 - Kotlinx datetime for dates & time management

### Testing
yes. 

I test the code on my local machine against my local Transmission client, but obviously that's not
very reproducible. Though I feel like it would be rather difficult to find a way to reliably test this
(set up a container with Transmission + Rpc library?). If you have any ideas, consider contributing.

### Contributions

This project aims to support Transmission 4.0.0 to 4.1.0 (`rpc-version-semver` 5.3.0, `rpc-version`: 17),
the current `stable` branch. If you want to contribute, please add me on Discord or Matrix: `croissant676`.

