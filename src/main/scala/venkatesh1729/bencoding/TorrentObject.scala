package venkatesh1729.bencoding

case class FInfo(length: Long, md5Sum: Option[String], path: String = "")

case class FileInfo(pieceLength: Long, pieces: Seq[Byte], _private: Option[Long], name: String, files: Seq[FInfo])

case class TorrentObject(info: FileInfo, announce: String, announceList: Option[Seq[Seq[String]]],
  creationDate: Option[Long], comment: Option[String], createdBy: Option[String], encoding: Option[String])

