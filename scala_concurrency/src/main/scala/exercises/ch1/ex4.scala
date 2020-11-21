//package exercises.ch1
//
//object ex4 extends App {
//  class SyncVar[T] {
//    var shared: T = null.asInstanceOf[T]
//    def isEmpty: Boolean = this.synchronized{
//      shared == null
//    }
//    def nonEmpty: Boolean = this.synchronized{
//      shared != null
//    }
//    def get: T = this.synchronized{shared}
//    def put(x: T): Unit = this.synchronized(shared = x)
//
//  }
//}
