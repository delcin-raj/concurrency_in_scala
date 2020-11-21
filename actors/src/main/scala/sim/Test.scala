object Global {
  var time = 0
  var currFloor = 1
}

object Test extends App {
  Global.time = 9
  println(f"${Global.time} -> ${Global.currFloor}")
}

