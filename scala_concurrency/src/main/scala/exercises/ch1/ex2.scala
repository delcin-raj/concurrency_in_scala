package exercises.ch1
import concurrency.utils.CustomThread._
object ex2 extends App {
  def periodically(duration: Long)(b: =>Unit): Unit = {
    val worker = new Thread {
      while (true) {
        b
        Thread.sleep(duration)
      }
    }
    worker.setDaemon(true)
    worker.start()
  }

  def f(b: =>Unit) = {
    b
  }
  f{
    println("something")
    def fact(n: Int): Int = if(n==0) 1 else n*fact(n-1)
    val r = fact(6)
    println(r)
  }
}
