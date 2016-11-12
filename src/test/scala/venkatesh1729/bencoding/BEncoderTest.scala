package venkatesh1729.bencoding

import org.scalatest.{ WordSpec, WordSpecLike }

class BEncoderTest extends WordSpec with WordSpecLike {

  "String encoder" should {
    "should encode empty string correctly" in {
      assert(StringEncoder.encode("") == "0:".getBytes.toSeq)
    }
    "should encode generic strings correclty" in {
      val sentence = "Scala-Is-Awesome"
      assert(StringEncoder.encode(sentence) == s"${sentence.length}:$sentence".getBytes.toSeq)
    }
  }

  "Long encoder" should {
    "should encode 0 correctly" in {
      assert(LongEncoder.encode(0: Long) == s"i${0}e".getBytes.toSeq)
    }
    "should encode negative and positive numbers correctly" in {
      val ramanujanNumber: Long = 1729
      assert(LongEncoder.encode(ramanujanNumber) == s"i1729e".getBytes.toSeq)
      assert(LongEncoder.encode(-1 * ramanujanNumber) == s"i-1729e".getBytes.toSeq)
    }
  }

  "List encoder" should {
    "should encode empty list correctly" in {
      assert(ListEncoder.encode(Seq()) == "le".getBytes.toSeq)
    }
    "should encode generic lists correctly" in {
      val list = Seq("Scala": BString, "Is": BString, "Awesome": BString)
      assert(ListEncoder.encode(list) == s"l5:Scala2:Is7:Awesomee".getBytes.toSeq)
    }
  }

  "Dictionary encoder" should {
    "should encode empty dictionary correctly" in {
      assert(DictEncoder.encode(Map.empty[BString, BObject]) == "de".getBytes.toSeq)
    }
    "should encode generic dictionaries correctly with keys in sorted order" in {
      val dict: Map[BString, BObject] = Map("Scala" -> "1", "Is" -> "2", "Awesome" -> "3")
        .map(pair => (pair._1: BString, pair._2: BString))
      assert(DictEncoder.encode(dict) == s"d7:Awesome1:32:Is1:25:Scala1:1e".getBytes.toSeq)
    }
  }

  private implicit def stringToBString(s: String): BString = BString(s.getBytes: _*)
  private implicit def longToBLong(s: Long): BLong = BLong(s)
  private implicit def toBList(l: Seq[BObject]): BList = BList(l: _*)
  private implicit def toBDict(d: Map[BString, BObject]): BDict = BDict(d)
}
