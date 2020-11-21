import java.lang.Thread
object ThreadUnprotectedUid extends App {
  var uidCount = 0
  def getUid() = this.synchronized {
    val next = uidCount + 1
    uidCount = next
    next
  }

  def thread(body: => Unit): Thread = {
    val t = new Thread {
      override def run() = body
    }
    t.start
    t
  }

  def printUids(n: Int): Unit = {
    val uids = for (i <- 1 to n) yield getUid()
    log(f"generated uids: $uids")
  }

  def log(s: String): Unit = println(s + f"-> ${Thread.currentThread.getName}")
  val t = thread(printUids(100))
  printUids(100)
  t.join()
}

