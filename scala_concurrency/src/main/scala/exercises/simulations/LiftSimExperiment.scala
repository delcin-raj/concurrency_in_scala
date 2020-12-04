package exercises.simulations

import scala.collection.mutable

object LiftSimExperiment extends App {
  val numFloors = 3
  val numLifts = 3
  val lifts = new Array[Lift](numLifts)
  val floors = new Array[Floor](numFloors)

  val rand = scala.util.Random

  // The key of the wait status is the floor and the value is a boolean
  // which indicates whether it's waitList is empty or not
  val waitStatus = new mutable.HashMap[Int,Boolean]() withDefaultValue false

  class Passenger(val id: Int,val source: Int,val destination: Int){
    override def toString: String = {
      f"I travelled from $source to $destination, my name is $id"
    }
  }
  class Floor(id: Int) extends Thread{
    // List of passengers waiting in a floor
    // They will get into one of the available lifts
    // I have not defined any constraints for lift
    // So the lift that arrives first will dequeue the passengers
    // from waitList
    val waitList = new mutable.Queue[Passenger]()
    // arrivedList keeps track of the passengers who comes to the floor
    val  arrivedList= new mutable.Stack[Passenger]()

    val dispatchLock = new Object
    val receiveLock = new Object

    override def run(): Unit = {
      Thread.sleep(1000)
      while(true) {
        if (waitList.nonEmpty) {
          for (x <- lifts) {
            // Every request takes some time to make varies from 1 to 2 seconds
            Thread.sleep((rand.nextInt(2) + 1) * 1000) // sleeps for 100 milliseconds 10^3 millisecond is 1 second
            x.makeRequests(id)
          }
          waitStatus(id) = true
        }
      }
    }
    // A lift is going to call dispatch on only one Floor
    // So it is not possible for multiple floors to mutate the passengers queue of a Lift
    // So we don't need any lock
    def dispatch(passengers: mutable.Queue[Passenger]): Unit = {
      while (dispatchLock.synchronized(waitList.nonEmpty)) {
        // It is doubly guarded so that when more than one list reaches a floor
        // different passengers can enter different lifts.
        // If only one lock was used for the entire while loop then only one lift will be filled
        dispatchLock.synchronized{
        if (waitList.nonEmpty) {
          // passengers are getting into the lift
          // let say it takes 1 to 3 second to dispatch 1 passenger
          Thread.sleep((rand.nextInt(3) + 1) * 1000)
          passengers.enqueue(waitList.dequeue())
        }
      }
      }
      waitStatus(id) = false
    }



    def receivePassengers(incoming: mutable.Queue[Passenger],liftId: Int): Unit = {
      // the if condition below checks whether all passengers gets dropped to their destination
      if (incoming.forall(x=>x.destination == id)) {
        for (p <- incoming) receiveLock.synchronized{
          // similarly it takes 1 to 10 seconds for a passenger to get out of the
          Thread.sleep((rand.nextInt(10) + 1) * 1000)
          arrivedList.push(p)
          println(p + " from " + liftId)
        }
      } else {
        println("There is a mistake")
      }
    }

  }

  class Lift(id: Int) extends Thread {
    // Lift will dequeue while the passengers will enqueue
    // the value requests is immutable which is a pointer to a mutable Queue
    val requests = new mutable.Queue[Int]()
    val lock = new Object

    // Once reaching a floor we have to offload all the passengers belonging to that floor
    // irrespective of their order. So we have to apply filter
    // Only the lift modifies the passengers queue so no lock is required
    var passengers = new mutable.Queue[Passenger]()

    override def run(): Unit = {
      Thread.sleep(1000)
      while (true) lock.synchronized{
        if (requests.isEmpty && passengers.isEmpty) lock.wait()
        // else either passengers or requests is non Empty or both
        if (passengers.nonEmpty) {
          Thread.sleep((rand.nextInt(5) + 1) * 1000)
          drop(passengers.front.destination)
        }
        if (requests.nonEmpty) {
          Thread.sleep((rand.nextInt(5) + 1) * 1000)
          onBoard(requests.dequeue())
        }
      }
    }

    def makeRequests(floor: Int): Unit = lock.synchronized{
      Thread.sleep((rand.nextInt(5) + 1) * 1000)
      requests.enqueue(floor)
      lock.notifyAll()
    }

    def onBoard(floor: Int): Unit = {
      if (waitStatus(floor)) {
        floors(floor).dispatch(passengers)
      }

    }

    def drop(destination: Int): Unit = {
      Thread.sleep((rand.nextInt(5) + 1) * 1000)
      val (toDrop,remaining) = passengers.partition(x=> x.destination == destination)
      floors(destination).receivePassengers(toDrop,id)
      passengers = remaining
      floors(destination).dispatch(passengers)
    }
  }

  def createPassengers(num: Int): List[Passenger] = {
    if (num <= 0) List()
    else {
      new Passenger(num,rand.nextInt(numFloors),rand.nextInt(numFloors)) :: createPassengers(num - 1)
    }
  }

  // creating the floors
  for (i <- 0 until numFloors) {
    floors(i) = new Floor(i)
  }

  // creating lifts
  for (i <- 0 until numFloors) {
    floors(i) = new Floor(i)
  }

  // starting floor
  for (i <- 0 until numFloors) {
    floors(i).start()
  }

  // starting lifts
  for (i <- 0 until numLifts) {
    lifts(i).start()
  }

  // creating passengers
  val passengers = createPassengers(1000)

  // adding passengers to the waitList of the the source floor
  for (p <- passengers) {
    floors(p.source).waitList.enqueue(p)
  }

  floors(0).join()

}
