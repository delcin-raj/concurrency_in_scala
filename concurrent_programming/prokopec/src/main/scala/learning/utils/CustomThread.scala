package learning.utils
import java.lang.Thread

object ThreadsMain extends App {
  def thread(body: =>Unit): Thread = {
    val t = new Thread {
      override def run() = body
    }
    t.start()
    t
  }

  def log(s: String): Unit = println(s + f"-> ${Thread.currentThread.getName}")
}
