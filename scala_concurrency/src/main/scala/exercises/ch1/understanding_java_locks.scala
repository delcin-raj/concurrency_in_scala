package exercises.ch1
import concurrency.utils.CustomThread._

object understanding_java_locks extends App{
  object Shared {
    var sum: Int = 0
    // How to explain using integer variable as a lock?
    var x = AnyRef
    def dec(): Unit = {
      sum -= 1
    }
    def inc(): Unit = {
      sum += 1
    }
    def getSum: Int = sum
  }
  val t1 = thread{
    for (_ <- 1 to 10000) Shared.inc()
  }
  val t2 = thread{
    for (_ <- 1 to 1000) Shared.dec()
  }
  t1.join(); t2.join()
  println(Shared.getSum)
}
