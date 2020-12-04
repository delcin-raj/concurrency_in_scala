package exercises.simulations

import scala.collection.mutable

object ActorSim {
  val numActors = 2;
  val actors = new Array[Actor](2)

  class Mail(val senderID: Int, val receiverID: Int, val message: String)

  class Actor extends Thread {
    val mails = new mutable.Queue[Mail]()
    def receive(mail: Mail):Unit = this.synchronized {
      mails.enqueue(mail) // same as this.mails.enqueue(mail). In scala 'this' is implicitly understood by compiler
    }
    def send(mail: Mail): Unit = {
      actors(mail.receiverID).receive(mail) // in scala inorder to access an index of an array you would do array(index)
    }
  }
  // Create actors and experiment here
}
