package concurrency.utils
object CustomThread {
  def thread(job: =>Unit): Thread = {
    val t = new Thread {
      override def run() = job
    }
    t.start()
    t
  }

  def log(s: String): Unit = println(s + f"-> ${Thread.currentThread.getName}")

  var uidCount = 0L
  def getUid() = {
    val freshuid = uidCount + 1
    uidCount = freshuid
    freshuid
  }
}
