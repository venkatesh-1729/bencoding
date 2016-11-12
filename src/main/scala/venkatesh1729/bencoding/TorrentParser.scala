package venkatesh1729.bencoding

import java.nio.file.{ Files, Paths }
import scala.util.{ Failure, Success, Try }

object TorrentParser {

  private def getInfo(infoDict: BDict): FileInfo = {
    val pieceLength: Long = infoDict("piece length").asInstanceOf[BLong].long
    val pieces: Seq[Byte] = infoDict("pieces").asInstanceOf[BString].bytes
    val _private: Option[Long] = infoDict.get("private").map(_.asInstanceOf[BLong].long)

    val name: String = infoDict("name").asInstanceOf[BString].toString
    val files: Seq[FInfo] = if (!infoDict.dict.contains("files")) {
      // MultiFile Torrent
      val length: Long = infoDict("length").asInstanceOf[BLong].long
      val md5sum: Option[String] = infoDict.get("md5sum").map(_.asInstanceOf[BString].toString)
      Seq(FInfo(length, md5sum))
    } else {
      // SingleFile Torrent
      val fileDicts: Seq[BDict] = infoDict("files").asInstanceOf[BList].list.map(_.asInstanceOf[BDict])
      fileDicts.map({
        fileDict =>
          val length: Long = fileDict("length").asInstanceOf[BLong].long
          val md5sum: Option[String] = fileDict.get("md5sum").map(_.asInstanceOf[BString].toString)
          val path: String = fileDict("path").asInstanceOf[BList].list.map(_.asInstanceOf[BString].toString).mkString("/")
          FInfo(length, md5sum, path)
      })
    }
    FileInfo(pieceLength, pieces, _private, name, files)
  }
  def parseTorrentFile(filePath: String): Either[Exception, TorrentObject] = {

    val bytes: Array[Byte] = Files.readAllBytes(Paths.get(filePath))
    val bObj = BParser.parseBObject(bytes)
    bObj match {
      case Some(bDict: BDict) =>
        val tObj: Try[TorrentObject] = Try {
          val infoDict: BDict = bDict("info").asInstanceOf[BDict]
          val announce: String = bDict("announce").asInstanceOf[BString].toString
          val announceList: Option[Seq[Seq[String]]] = bDict.get("announce-list").map(_.asInstanceOf[BList].list.
            map(_.asInstanceOf[BList].list.map(_.asInstanceOf[BString].toString)))
          val creationDate: Option[Long] = bDict.get("creation date").map(_.asInstanceOf[BLong].long)
          val comment: Option[String] = bDict.get("comment").map(_.asInstanceOf[BString].toString)
          val createdBy: Option[String] = bDict.get("created by").map(_.asInstanceOf[BString].toString)
          val encoding: Option[String] = bDict.get("encoding").map(_.asInstanceOf[BString].toString)
          val info: FileInfo = getInfo(infoDict)
          TorrentObject(info, announce, announceList, creationDate, comment, createdBy, encoding)
        }
        tObj match {
          case Success(validObj) => Right(validObj)
          case Failure(ex) => Left(new Exception(ex))
        }
      case others =>
        Left(new Exception("Unable to parse torrent file"))
    }
  }

  private implicit def stringToBString(s: String): BString = BString(s.getBytes: _*)
  private implicit def longToBLong(s: Long): BLong = BLong(s)

}
