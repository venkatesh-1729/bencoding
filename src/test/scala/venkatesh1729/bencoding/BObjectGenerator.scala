package venkatesh1729.bencoding

import org.scalacheck.Arbitrary._
import org.scalacheck.Gen
import scala.math.Ordering.Implicits._

object BObjectGenerator {

  implicit val bString: Gen[BString] = arbitrary[Seq[Byte]].map(s => s: BString)
  implicit val bLong: Gen[BLong] = arbitrary[Long].map(l => l: BLong)
  implicit val bList: Gen[BList] = bList(0, List())
  implicit val bDict: Gen[BDict] = bDict(0, List())
  implicit val bObjs: Gen[BObject] = bObjs(0)

  private implicit def toBString(b: Seq[Byte]): BString = BString(b: _*)
  private implicit def toBLong(l: Long): BLong = BLong(l)
  private implicit def toBList(l: Seq[BObject]): BList = BList(l: _*)
  private implicit def toBDict(d: Map[BString, BObject]): BDict = BDict(d)

  private val maxDepth = 10

  private def bList(level: Int, acc: List[BObject]): Gen[BList] = {
    if (level > maxDepth) {
      Gen.map(_ => BList(acc))
    } else {
      for {
        obj <- bObjs(level + 1)
        b <- arbitrary[Boolean]
        f <- if (b) Gen.map(_ => BList(acc)) else bList(level + 1, obj :: acc)
      } yield f
    }
  }
  private def bDict(level: Int, acc: List[(BString, BObject)]): Gen[BDict] = {
    if (level > maxDepth) {
      Gen.map(_ => acc.sortBy(pair => pair._1.bytes.toList).toMap)
    } else {
      for {
        str <- bString
        obj <- bObjs(level + 1)
        b <- arbitrary[Boolean]
        f <- {
          if (b) { Gen.map(_ => BDict(acc.sortBy(pair => pair._1.bytes.toList).toMap)) }
          else { bDict(level + 1, (str, obj) :: acc) }
        }
      } yield f
    }
  }
  private def bObjs(level: Int): Gen[BObject] = {
    if (level > maxDepth) {
      Gen.oneOf(bString, bLong)
    } else {
      val x: BString = bString.sample.get
      val y: BObject = Gen.oneOf(bString, bLong).sample.get
      Gen.oneOf(Gen.oneOf(bString, bLong, bList(level + 1, List(x)), bDict(level + 1, List((x, y)))), bObjs(level + 1))
    }
  }

}
