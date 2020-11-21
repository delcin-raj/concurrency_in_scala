package exercises.ch1
import concurrency.utils.CustomThread._
import exercises.ch1.ArraySum.sum
object ArraySum extends App {
  var sum: Int = 0
  val lock = AnyRef // => new Object()
  // three threads are trying to obtain the monitor "lock"
  // But only one will obtain it at any instant of time
  // And that thread alone is eligble to execute that synchronized block of code
  def inc: Unit = lock.synchronized{sum += 1}
  val t1 = thread{
    for (i <- (1 to 100)) {
      inc
    }
  }
  val t2 = thread{
    for (i <- (1 to 100)) {
      inc
    }
  }
  val t3 = thread{
    for (i <- (1 to 100)) {
      inc
    }
  }
  t1.join();t2.join();t3.join()
  println(sum)
}
