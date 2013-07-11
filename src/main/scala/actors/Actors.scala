package actors

import akka.actor.{Actor, ActorRef, Cancellable}
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.duration._
import actors.utils.Utils

object EnvironmentMessages {

}

object Messages {
  case class PushRequestFS(remoteWorker: ActorRef)
  case class PushRequestIC(remoteWorker: ActorRef,files: List[String])
  case class PushRequestPC(remoteWorker: ActorRef,files: List[String], period: FiniteDuration)


  case class RequestFS()
  case class FS(filesystem: List[String])

  case class Error()

  case class InitialCheck(files: List[String])
  case class InitialCheckResult(result: Map[String, Long])

  case class PeriodicCheck(files: List[String], period: FiniteDuration)
  case class PeriodicCheckResult(result: Either[List[String],Boolean])
  case class PeriodicCheckUpdate(files: List[String])
  case class PeriodicCheckStop()

}

class PeriodicalCheckActor extends Actor {
  import Messages._
  import context.{dispatcher,system}
  import model.FSStorage._

  var task: Option[Cancellable] = None

  def receive = {
    case PeriodicCheck(files,dur) =>
      val master = sender

      Future {
        val oldCRC = workerCRCMaps.get("this worker name").get
        val currentCRC = Utils.generateMap(files)
        Utils.compareCRCMaps(oldCRC,currentCRC)
      }.onComplete {
        case Success(result) =>
          master ! PeriodicCheckResult(result)
          task = Some(system.scheduler.scheduleOnce(dur, self, PeriodicCheck(files,dur)))
        case Failure(failure) =>
          println("exception occured:" +failure.toString)
      }

    case PeriodicCheckStop() =>
      task.foreach(_.cancel())

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
