package venkatesh1729.bencoding

import java.net.URL

import org.scalatest.{ WordSpec, WordSpecLike }
import venkatesh1729.bencoding.TorrentParser.parseTorrentFile

class TorrentObjectTest extends WordSpec with WordSpecLike {

  "A valid torrent file" should {
    "should be parsed correctly with correct fields" in {
      val source: URL = getClass.getResource("/ubuntu-16.04-desktop-amd64.iso.torrent")
      val tObj: Either[Exception, TorrentObject] = parseTorrentFile(source.getPath)
      assert(tObj.isRight && tObj.right.get.info.name == "ubuntu-16.04-desktop-amd64.iso")
    }
  }

}