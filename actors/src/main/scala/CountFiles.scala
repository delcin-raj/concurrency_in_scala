import akka.actor._
import java.io._
import akka.routing._

class FileExplorer extends Actor {
  def receive = {
    // FileExplorer at a single instance will only explore one directory
    // It will not search further
    case dirName : String => {
      val file = new File(dirName) // opening the directory
      val children = file.listFiles() // children has the list of all files(inclusive of directories)
      var filesCount = 0


      // FileExplporer will send the directories one by one to the sender
      // Who is that sender?
      if(children != null) {
        // sending the directories to FilesCounter
        // The akka library keeps track of the sender
        children.filter (_.isDirectory). // filter out all the directories in the children list to result list
          foreach(sender !_.getAbsolutePath) // for each element in the result send them back to the FC
        
        // sending the filecounts to FilesCounter
        filesCount = children.count(!_.isDirectory) // counting all the files in children which is not a directory
      }
      sender ! filesCount
    }
  }
}

class FilesCounter extends Actor {
  val start = System.nanoTime
  var filesCount = 0L
  var pending = 0 //denotes number of directories yet to explore

  // fileExplorers is an actor which maintains 100 actors under its supervision
  // The scheduling scheme is roundRobin
  // creating the workers and the supervisor
  val fileExplorers =
    context.actorOf(RoundRobinPool(8).props(Props[FileExplorer])) // creating 100 FileExplorers and we will manage

  def receive = {
    case dirName : String => {
      pending = pending + 1
      fileExplorers ! dirName
    }

    case count : Int => {
      filesCount = filesCount + count
      pending = pending - 1
    }

   // doubt is how to make sure the dirReceive is before the 
   // count receive? 
   // routing similar to TCP?
    if (pending == 0) {
      val end = System.nanoTime
      println(s"Files count: $filesCount")
      println(s"Time taken: ${(end-start)/1.0e9} seconds")
      context.system.terminate()
    }
  }
}

object CountFiles extends App {
  val system = ActorSystem("sample")
  val filesCounter = system.actorOf(Props[FilesCounter])

  filesCounter ! args(0)
}

