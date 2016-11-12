package venkatesh1729.bencoding

import java.nio.charset.StandardCharsets.ISO_8859_1
import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.{ Position, Reader }

case object BParser extends Parsers {

  type Elem = Byte

  private def byteParser: Parser[Byte] = (in: Input) => {
    if (in.atEnd) { throw new Exception("End of input") }
    else { Success(in.first, in.rest) }
  }

  private def digitParser: Parser[Byte] = acceptIf({ _.toChar.isDigit })(_ => "Expected a digit")
  private def nonZeroDigitParser: Parser[Byte] = acceptIf({ c => c.toChar.isDigit && c != '0' })(_ => "Expected a non zero digit")
  private def stringParser: Parser[BString] = digitParser.+.^^(bytes => new String(bytes.toArray).toInt) <~ ':' >> (repN(_, byteParser))
  private def longParser: Parser[BLong] = 'i' ~> (('-' ~ nonZeroDigitParser ~ digitParser.*)
    .map({ case x ~ y ~ z => x :: y :: z }) | digitParser.+).^^(bytes => { new String(bytes.toArray).toLong }) <~ 'e'
  private def listParser: Parser[BList] = 'l' ~> rootParser.* <~ 'e'
  private def dictParser: Parser[BDict] = 'd' ~> (stringParser ~ rootParser).^^({ case k ~ v => (k, v) }).* <~ 'e'
  private def rootParser: Parser[BObject] = stringParser | longParser | listParser | dictParser

  def parseBObject(input: Seq[Byte]): Option[BObject] = {
    phrase(rootParser)(ByteReader(input)) match {
      case Success(result, _) => Some(result)
      case other => None
    }
  }

  private implicit def toByteParser(c: Char): Parser[Byte] = elem(c.toByte)
  private implicit def toBLongParser(lp: Parser[Long]): Parser[BLong] = lp ^^ BLong
  private implicit def toBStringParser(sp: Parser[Seq[Byte]]): Parser[BString] = sp ^^ (BString(_: _*))
  private implicit def toBListParser(lp: Parser[Seq[BObject]]): Parser[BList] = lp ^^ (BList(_: _*))
  private implicit def toBDictParser(dp: Parser[Seq[(BString, BObject)]]): Parser[BDict] = dp ^^ (seq => BDict(seq.toMap))

  private case class ByteReader(bytes: Seq[Byte]) extends Reader[Byte] {

    case class BytePosition(bytes: Seq[Byte]) extends Position {
      var column: Int = 1
      def line: Int = 1
      protected def lineContents: String = new String(bytes.toArray, ISO_8859_1)
    }

    override def first: Byte = bytes.head
    override def rest: Reader[Byte] = if (atEnd) this else ByteReader(bytes.tail)
    override def pos: Position = BytePosition(bytes)
    override def atEnd: Boolean = if (bytes.isEmpty) true else false
  }

}

