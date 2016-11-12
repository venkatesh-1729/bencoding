package venkatesh1729.bencoding

sealed trait BObject

case class BString(bytes: Byte*) extends BObject {
  override def toString: String = new String(bytes.toArray)
}

case class BLong(long: Long) extends BObject

case class BList(list: BObject*) extends BObject

case class BDict(dict: Map[BString, BObject]) extends BObject {
  def get(bString: BString): Option[BObject] = dict.get(bString)
  def apply(bString: BString): BObject = dict(bString)
}
