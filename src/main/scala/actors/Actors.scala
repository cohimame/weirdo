package actors

import akka.actor.{Actor, ActorRef}
import scala.concurrent.Future
import scala.util.{Failure, Success}

object EnvironmentMessages {

}

object Messages {
  case class PushRequestFS(remoteWorker: ActorRef)
  case class PushRequestIC(remoteWorker: ActorRef,files: List[String])

  case class RequestFS()
  case class FS(filesystem: List[String])
  case class Error()

  case class InitialCheck(files: List[String])
  case class InitialCheckResult(result: Map[String, Long])
}

class InitialCheckActor extends Actor {
  import Messages._
  import context.dispatcher

  def receive = {
    case InitialCheck(files) =>
      val master = sender
      Future {
        Utils.generateMap(files)
      }.onComplete {
        case Success(result) =>
          master ! InitialCheckResult(result)
        case Failure(failure)=>
          master ! Error()
      }
  }

}

class InitialCheckRequester extends Actor {
  import Messages._
  import model.FSStorage._

  def receive = {
    case PushRequestIC(worker,files) => {
      worker ! InitialCheck(files)
    }
    case InitialCheckResult(result) =>
      workerCRCMaps += (sender.path.toString -> result)
  }

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

class FileSystemActor extends Actor {
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
