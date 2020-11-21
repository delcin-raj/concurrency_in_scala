object ThreadsCreation extends App {
  def thread(body: =>Unit): Thread = {
    val t = new Thread {
      override def run() = body
    }
    t.start()
    t
  }
  def log(s: String): Unit = println(s + f"-> ${Thread.currentThread.getName}")
 // class MyThread extends Thread {
 //   override def run(): Unit = {
 //     println(f"New thread with name ${Thread.currentThread.getName}")
 //   }
 // }
 // for (i <-0 until 5) {
 //   val t = new MyThread
 //   t.start()
 //   t.join()
 //   println("Joined")
    val t = thread(log("New thread running"))
    println(f"${t.getName}")
    log("....")
    log("....")
    log("....")
    t.join()
    log("Joined")
}
