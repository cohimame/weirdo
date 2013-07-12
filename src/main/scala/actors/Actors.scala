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
  case class PushRequestPCStop(worker: ActorRef)

  case class RequestFS()
  case class FS(filesystem: List[String])

  case class Error()

  case class InitialCheck(files: List[String])
  case class InitialCheckOk()

  case class PeriodicCheck(files: List[String], period: FiniteDuration)
  case class PeriodicCheckResult(result: Either[List[String],Boolean])

  case class PeriodicCheckStop()

}

class PeriodicCheckRequester extends Actor {
  import Messages._
  def receive = {

    case PushRequestPC(worker,files,period) =>
      worker ! PeriodicCheck(files,period)

    case PushRequestPCStop(worker) =>
      worker ! PeriodicCheckStop()

    case PeriodicCheckResult(result) =>
      result match {
        case Left(left) => println(left.mkString("\n"))
        case Right(bool) => println("success!")
      }
  }
}

class PeriodicalCheckActor extends Actor {
  import Messages._
  import context.{dispatcher,system}

  var task:Option[Cancellable] = None

  def receive = {

    case PeriodicCheck(files, period) =>
      val master = sender
      Future {
        val oldMap = model.DataStorage.workerCRCMaps
        val currentMap = Utils.generateMap(files)
        Utils.compareCRCMaps(oldMap,currentMap)
      }.onComplete {
        case Success(result) =>
          master ! PeriodicCheckResult(result)
          task = Some(system.scheduler.scheduleOnce(period, self, PeriodicCheck(files,period)))
        case Failure(failure) =>
          println("exception occured:" +failure.toString)
      }

    case PeriodicCheckStop() =>
      task foreach( t => t.cancel())

  }
}

class InitialCheckRequester extends Actor {
  import Messages._

  def receive = {
    case PushRequestIC(worker,files) => {
      worker ! InitialCheck(files)
    }
    case InitialCheckOk() =>
      println("initial check done")
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
          model.DataStorage.workerCRCMaps = result
          master ! InitialCheckOk()
        case Failure(failure)=>
          master ! Error()
      }
  }

}

class FileSystemRequester extends Actor {
  import Messages._
  def receive = {
    case PushRequestFS(worker) => {
      worker ! RequestFS()
    }
    case FS(list) => {
      model.DataStorage.workerFileSystem = list
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
