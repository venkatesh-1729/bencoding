package venkatesh1729.bencoding

import org.scalacheck.Prop.forAll
import BObjectGenerator._
import org.scalacheck.{ Arbitrary, Gen, Properties }

class BParserTest extends Properties("Bencoding Objects Parsing") {

  property("Encoded strings should be parsed correctly") = {
    forAll { (s: BString) =>
      val parsedBObj = BParser.parseBObject(StringEncoder.encode(s))
      parsedBObj.isDefined && parsedBObj.get == s
    }
  }

  property("Encoded longs should be parsed correctly") = {
    forAll { (l: BLong) =>
      val parsedBObj = BParser.parseBObject(LongEncoder.encode(l))
      parsedBObj.isDefined && parsedBObj.get == l
    }
  }

  property("Encoded lists should be parsed correctly") = {
    forAll { (l: BList) =>
      val parsedBObj = BParser.parseBObject(ListEncoder.encode(l))
      parsedBObj.isDefined && parsedBObj.get == l
    }
  }

  property("Encoded dictionaries should be parsed correctly") = {
    forAll { (d: BDict) =>
      val parsedBObj = BParser.parseBObject(DictEncoder.encode(d))
      parsedBObj.isDefined && parsedBObj.get == d
    }
  }

  implicit def arbBObj[T](implicit obj: Gen[T]): Arbitrary[T] = Arbitrary(obj)

}
