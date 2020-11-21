package sim

import scala.collection.mutable.{Map,Queue}

object Global {
  var time = 0 // seconds
  var liftLocation = 0 // Ground Floor
  var numPassengers = 0 // Initially
}

class Lift() extends Actor {
  var floorReqs = new Queue[Int]() // Keep track of requests from floors source
  var passengers = new Map[Int,String]() // Keep track of destination and corresponding passengers
  var stops = new Queue[Int]()
  
  def receive = {
    case floor: Int => floorReqs += floor // enqueing the incoming request
  }
  



  // Lift is going to receive 
