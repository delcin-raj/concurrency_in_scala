package exercises.ch1

import concurrency.utils.CustomThread._

object ex1 extends App {
  def parallel[A,B](a: => A, b: => B): (A,B) = {
    var resa: A = null.asInstanceOf[A]
    var resb: B = null.asInstanceOf[B]
    val t1 = thread{resa = a}
    val t2 = thread{resb = b}
    t1.join(); t2.join()
    (resa,resb)
  }
  def fact(n: Int): Int = if (n==0) 1 else n*fact(n-1)
  println(parallel(fact(9),fact(7)))
}
