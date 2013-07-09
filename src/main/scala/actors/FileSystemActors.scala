package actors

import akka.actor.{Actor, ActorRef}
import scala.concurrent.Future
import scala.util.{Failure, Success}

object Messages {
  case class PushRequestFS(remoteWorker: ActorRef)
  case class RequestFS()
  case class FS(filesystem: List[String])
  case class Error()
}


class FileSystemRequester extends Actor {
  import Messages._
  import model.FSStorage._

  def receive = {
    case PushRequestFS(worker) => {
      worker ! RequestFS()
    }
    case FS(list) => {
      workerFileSystem += (sender.path.toString -> list)
    }
    case Error => {
      println(sender.path.toString + " meet an error while scanning his filesystem ")
    }
  }

}

class FileSystemProvider extends Actor {
  import Messages._
  import context.dispatcher

  def receive = {
    case RequestFS() => {
      val master = sender
      Future {
        Utils.getFileSystem()
      }.onComplete {
        case Success(list) =>
          master ! FS(list)
        case Failure(exception) =>
          master ! Error
          println(exception)
      }
    }
  }

}
