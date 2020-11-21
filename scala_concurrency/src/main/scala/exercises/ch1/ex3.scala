package exercises.ch1

object ex3 extends App {
  class SyncVar[T] {
    private var shared:T = null.asInstanceOf[T]
    private var isEmpty = true

    // Now synchronize read and write
    def get(): T = this.synchronized{
      if (isEmpty) throw new Exception("Nothing to share")
      else {
        isEmpty = true
        val v = shared
        shared = null.asInstanceOf[T]
        v
      }
    }

    def put(x: T): Unit = this.synchronized{
      if (isEmpty) {
        shared = x
        isEmpty = false
      }
      else throw new Exception("Full")
    }
  }
}
