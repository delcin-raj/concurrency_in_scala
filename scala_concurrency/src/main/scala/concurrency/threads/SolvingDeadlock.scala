package concurrency.threads
import  concurrency.utils.CustomThread._
object SolvingDeadlock {
  class Account(val name: String, var money: Int) {
    val uid = getUid()
  }

  def send(a1: Account,a2: Account,n: Int): Unit = {
    def adjust(): Unit = {
      a1.money -= n
      a2.money += n
    }
    if (a1.uid < a2.uid) a1.synchronized(a2.synchronized(adjust()))
    else a2.synchronized(a1.synchronized(adjust()))
  }
  // should we create new thread for every possible function calls?
  // threads have their own stack space and are expensve
}
