# Bencode Library
>Bencode (pronounced like B encode) is the encoding used by the peer-to-peer file sharing system BitTorrent for storing and transmitting loosely structured data -- **Wikipedia**

## Features
1. Encode `Strings`, `Longs`, `Lists`, `Dictionaries` in Bencode
2. Decode byte streams to `Bencode Objects`
3. Read `.torrent` files and extract its fields

## Examples
### Creating Bencode Object
```
scala> import venkatesh1729.bencoding._
import venkatesh1729.bencoding._

scala> val str: String = "Hello World!"
str: String = Hello World!

scala> val bStr: BString = BString(str.getBytes:_*)
bStr: venkatesh1729.bencoding.BString = Hello World!

scala> val bLong: BLong = BLong(42)
bLong: venkatesh1729.bencoding.BLong = 42

scala> val bList: BList = BList(bStr, bLong)
bList: venkatesh1729.bencoding.BList = [Hello World!, 42]

scala> val bDict: BDict = BDict(Map(bStr -> bLong))
bDict: venkatesh1729.bencoding.BDict = {Hello World!:42}
```
### Encoding Bencode Objects
```
scala> StringEncoder.encode(bStr)
res5: Seq[Byte] = ArrayBuffer(49, 50, 58, 72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100, 33)

scala> LongEncoder.encode(bLong)
res6: Seq[Byte] = WrappedArray(105, 52, 50, 101)

scala> ListEncoder.encode(bList)
res7: Seq[Byte] = List(108, 49, 50, 58, 72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100, 33, 105, 52, 50, 101, 101)

scala> DictEncoder.encode(bDict)
res8: Seq[Byte] = List(100, 49, 50, 58, 72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100, 33, 105, 52, 50, 101, 101)
```
### Decoding Byte Streams
```
scala> BParser.parseBObject(StringEncoder.encode(bStr))
res0: Option[venkatesh1729.bencoding.BObject] = Some(Hello World!)

scala> BParser.parseBObject(LongEncoder.encode(bLong))
res1: Option[venkatesh1729.bencoding.BObject] = Some(BLong(42))

scala> BParser.parseBObject(ListEncoder.encode(bList))
res2: Option[venkatesh1729.bencoding.BObject] = Some(BList(List(Hello World!, BLong(42))))

scala> BParser.parseBObject(DictEncoder.encode(bDict))
res3: Option[venkatesh1729.bencoding.BObject] = Some(BDict(Map(Hello World! -> BLong(42))))
```

### Parsing Torrent File
```
scala> val tObj = TorrentParser.parseTorrentFile("/Users/venkatesh/Desktop/ubuntu-16.04-desktop-amd64.iso.torrent")
tObj: Either[Exception,venkatesh1729.bencoding.TorrentObject]

scala> tObj.right.get.announce
res9: String = http://torrent.ubuntu.com:6969/announce

scala> tObj.right.get.announceList
res10: Option[Seq[Seq[String]]] = Some(List(List(http://torrent.ubuntu.com:6969/announce), List(http://ipv6.torrent.ubuntu.com:6969/announce)))

scala> tObj.right.get.comment
res11: Option[String] = Some(Ubuntu CD releases.ubuntu.com)

scala> tObj.right.get.info.name
res12: String = ubuntu-16.04-desktop-amd64.iso
```

## Note
Although Scala comes with powerful out of the box parser combinators these shouldn't be used for heavy lifting parsing. You might want to look at [Parboiled](https://github.com/sirthias/parboiled2) and [fastparse](https://github.com/lihaoyi/fastparse). But It will be good exercise to implement with bencoding while learning Scala's parser combinators.