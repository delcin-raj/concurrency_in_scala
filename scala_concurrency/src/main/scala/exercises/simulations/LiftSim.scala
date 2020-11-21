package exercises.simulations

import scala.collection.mutable

object LiftSim extends App {
  val numFloors = 3
  val numLifts = 3
  val lifts = new Array[Lift](numLifts)
  val floors = new Array[Floor](numFloors)

  // The key of the wait status is the floor and the value is a boolean
  // which indicates whether it's waitlist is empty or not
  val waitStatus = new mutable.HashMap[Int,Boolean]() withDefaultValue false

  class Passenger(val name: String,val source: Int,val destination: Int){
    override def toString: String = {
      f"I travelled from $source to $destination, my name is $name"
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
      while(true) {
        if (waitList.nonEmpty) {
          for (x <- lifts) {
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
        if (dispatchLock.synchronized(waitList.nonEmpty)) {
          passengers.enqueue(waitList.dequeue())
        }
      }
      waitStatus(id) = false
    }



    def receivePassengers(incoming: mutable.Queue[Passenger],liftName: String): Unit = {
      // the if condition below checks whether all passengers gets dropped to their destination
      if (incoming.forall(x=>x.destination == id)) {
        for (p <- incoming) receiveLock.synchronized{
          arrivedList.push(p)
          println(p + " from " + liftName)
        }
      } else {
        println("There is a mistake")
      }
    }

  }

  class Lift(name: String) extends Thread {
    // Lift will dequeue while the passengers will enqueue
    // the value requests is immutable which is a pointer to a mutable Queue
    val requests = new mutable.Queue[Int]()
    val lock = new Object

    // Once reaching a floor we have to offload all the passengers belonging to that floor
    // irrespective of their order. So we have to apply filter
    // Only the lift modifies the passengers queue so no lock is required
    var passengers = new mutable.Queue[Passenger]()

    override def run(): Unit = {
      while (true) lock.synchronized{
        if (requests.isEmpty && passengers.isEmpty) lock.wait()
        // else either passengers or requests is non Empty or both
        if (passengers.nonEmpty) {
          drop(passengers.front.destination)
        }
        if (requests.nonEmpty) {
          onBoard(requests.dequeue())
        }
      }
    }

    def makeRequests(floor: Int): Unit = lock.synchronized{
      requests.enqueue(floor)
      lock.notifyAll()
    }

    def onBoard(floor: Int): Unit = {
      if (waitStatus(floor)) {
        floors(floor).dispatch(passengers)
      }

    }

    def drop(destination: Int): Unit = {
      val (toDrop,remaining) = passengers.partition(x=> x.destination == destination)
      floors(destination).receivePassengers(toDrop,name)
      passengers = remaining
      floors(destination).dispatch(passengers)
    }

  }
  val lift1 = new Lift("A")
  val lift2 = new Lift("B")
  val lift3 = new Lift("C")

  val floor0 = new Floor(0)
  val floor1 = new Floor(1)
  val floor2 = new Floor(2)
  floors(0) = floor0;floors(1)=floor1;floors(2)=floor2
  lifts(0) = lift1
  lifts(1) = lift2
  lifts(2) = lift3
  lift1.start();floor0.start();floor1.start();floor2.start()
  lift2.start(); lift3.start()

  // All you have to do is create different passengers and
  // put them into waitList of different floors
  floor0.waitList.enqueue(new Passenger("name",0,2))

  floor0.waitList.enqueue(new Passenger("0name1",0,2))
  floor0.waitList.enqueue(new Passenger("0name2",1,1))
  floor0.waitList.enqueue(new Passenger("0name3",2,0))
  floor0.waitList.enqueue(new Passenger("0name4",1,2))
  floor0.waitList.enqueue(new Passenger("0name5",0,2))

  floor1.waitList.enqueue(new Passenger("1name1",0,2))
  floor1.waitList.enqueue(new Passenger("1name2",1,0))
  floor1.waitList.enqueue(new Passenger("1name3",2,1))
  floor1.waitList.enqueue(new Passenger("1name4",1,0))
  floor1.waitList.enqueue(new Passenger("1name5",0,1))

  floor2.waitList.enqueue(new Passenger("2name1",0,2))
  floor2.waitList.enqueue(new Passenger("2name2",1,1))
  floor2.waitList.enqueue(new Passenger("2name3",2,0))
  floor2.waitList.enqueue(new Passenger("2name4",1,2))
  floor2.waitList.enqueue(new Passenger("2name5",0,2))
  //---------------//
  // None of the threads ever ceases their computation
  // Hence the program never halts as lift1 never terminates

  lift1.join()
}
