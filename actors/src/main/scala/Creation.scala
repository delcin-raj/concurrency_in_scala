import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.collection.mutable._
import scala.concurrent.Await
import scala.concurrent.duration._

case class Play(role: String)
case class ReportCount(role: String)

class HollywoodActor() extends Actor {
  val messagesCount : Map[String, Int] = Map()
  def receive = {
    case Play(role) => {
     val currentCount = messagesCount.getOrElse(role,0)
     messagesCount.update(role,currentCount + 1)
     println(s"sender ->$sender")
    }

    case ReportCount(role) =>
      // sender is an implicit value
      // we are sending the total count back to the sender
      // The sender should receive the message back and act on it
      sender ! messagesCount.getOrElse(role,0)
  }
}

class Director() extends Actor {
  def recive = {
    case  => 

object CreateActors extends App {
  val system = ActorSystem("sample")
  // val depp = system.actorOf(Props[HollywoodActor])

  val depp = system.actorOf(Props[HollywoodActor])
  val spiderHulk = system.actorOf(Props[HollywoodActor])

  // sending messages to depp and spiderHulk actors
  depp ! Play("Carribean")
  depp ! Play("Carribean")
  spiderHulk ! Play("Woohaa")
  spiderHulk ! Play("Woohaa")
  // Thread.sleep(200)
  depp ! Play("pirate")
  spiderHulk ! Play("buss buss slayer")
  depp ! Play("pirate")
  spiderHulk ! Play("buss buss slayer")
  depp ! Play("pirate")
  spiderHulk ! Play("buss buss slayer")


  // Asking the actors to report the calue
  implicit val timeout = Timeout(2.second)
  val carribeanFuture = depp ? ReportCount("Carribean")
  val woohaaFuture = spiderHulk ? ReportCount("Wooha")
  val pirateFuture = depp ? ReportCount("pirate")
  val bussFuture = spiderHulk ? ReportCount("buss buss slayer")
  
  // The model is asynchronous
  // We can basically do anything we want now
  // In synchronous system we have to wait for the messages to receive


  // now we have to wait for the future to return the value
  val carribeanCount = Await.result(carribeanFuture,timeout.duration)
  val woohaaCount = Await.result(woohaaFuture,timeout.duration)
  val pirateResult = Await.result(pirateFuture,timeout.duration)
  val bussCount = Await.result(bussFuture,timeout.duration)


  println(s"Im the main thread butches ${Thread.currentThread}")
  system.terminate()
}

