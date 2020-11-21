object Main {
  object Shared {
    private var sum: Int = 0
    def dec(): Unit = this.synchronized{
      sum -= 1
    }
    def inc(): Unit = this.synchronized{
      sum += 1
    }
    def getSum(): Int = sum
  }


}