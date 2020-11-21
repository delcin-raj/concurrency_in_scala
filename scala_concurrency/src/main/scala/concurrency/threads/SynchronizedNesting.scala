package concurrency.threads

import concurrency.utils.CustomThread._

object SynchronizedNesting extends App {
  import scala.collection.mutable.ArrayBuffer
  private val transfers = ArrayBuffer[String]()
  def logTransfer(name: String,n: Int): Unit = transfers.synchronized{
    transfers += f"transfer to account '$name' = $n"
  }
  class Account(val name: String, var money: Int)
  def add(account: Account,n: Int) = account.synchronized{
    account.money += n
    if(n > 10) logTransfer(account.name,n)
  }
  // synchronized actually synchronizes a block of code
  // it does not synchronize the object(ex., transfers) itself
  // Nested synchronization actually solves thus problem

  val jane = new Account("Jane",100)
  val john = new Account(name="john",money = 300)
  val t1 = thread( add(jane,20) )
  val t2 = thread( add(john,300) )
  val t3 = thread( add(jane,700) )
  t1.join; t2.join(); t3.join()
  log(f"---transfers ----\n$transfers")
  println(f"john -> ${john.money} jane -> ${jane.money}")
}



