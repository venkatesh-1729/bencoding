package venkatesh1729.bencoding

import scala.math.Ordering.Implicits._

sealed trait BEncoder[T <: BObject] {
  def encode(obj: T): Seq[Byte]
}

object BEncoder {
  def rootEncoder(obj: BObject): Seq[Byte] = {
    obj match {
      case bString: BString => StringEncoder.encode(bString)
      case bLong: BLong => LongEncoder.encode(bLong)
      case bList: BList => ListEncoder.encode(bList)
      case bDict: BDict => DictEncoder.encode(bDict)
    }
  }
}

case object StringEncoder extends BEncoder[BString] {
  override def encode(obj: BString): Seq[Byte] = {
    obj.bytes.size.toString.getBytes.toSeq ++ Seq(':'.toByte) ++ obj.bytes
  }
}

case object LongEncoder extends BEncoder[BLong] {
  override def encode(obj: BLong): Seq[Byte] = {
    ("i" + obj.long.toString + "e").getBytes
  }
}

case object ListEncoder extends BEncoder[BList] {
  override def encode(obj: BList): Seq[Byte] = {
    Seq('l'.toByte) ++ obj.list.flatMap(BEncoder.rootEncoder) ++ Seq('e'.toByte)
  }
}

case object DictEncoder extends BEncoder[BDict] {
  def encode(obj: BDict): Seq[Byte] = {
    Seq('d'.toByte) ++ obj.dict.toSeq.sortBy(pair => pair._1.bytes.toList)
      .flatMap(pair => BEncoder.rootEncoder(pair._1) ++ BEncoder.rootEncoder(pair._2)) ++ Seq('e'.toByte)
  }
}
