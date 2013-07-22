package worker

import akka.actor.{Props, Actor, ActorRef, Cancellable}
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.duration._
import actors.utils.Utils
import actors.Messages._

class WorkerActor(path: String) extends Actor {
  val fsScanner = context.actorOf(Props{new FileSystemActor(path)})
  val initChecker = context.actorOf(Props[InitialCheckActor])
  val periodicalChecker = context.actorOf(Props[PeriodicalCheckActor])

  def receive = {
    case msg @ RequestFS() => fsScanner forward msg
    case msg @ InitialCheck(files) => initChecker forward msg
    case msg @ PeriodicCheckStop() => periodicalChecker forward msg
    case msg @ PeriodicCheck(files, period) => periodicalChecker forward msg
  }

}

class PeriodicalCheckActor extends Actor {
  import context.{dispatcher,system}

  var task: Option[Cancellable] = None
  var master: ActorRef = _

  case class OnceMore(files: List[String], period: FiniteDuration)

  def receive = {

    case PeriodicCheck(files, period) =>
      task foreach( t => t.cancel())
      master = sender
      self ! OnceMore(files, period)

    case OnceMore(files, period) =>
      Future {
        val oldMap = model.WorkerDataStorage.workerCRCMaps
        val currentMap = Utils.generateMap(files)
        Utils.compareCRCMaps(oldMap,currentMap)
      }.onComplete {
        case Success(right @ Right(result)) =>
          master ! PeriodicCheckResult(right)
          task = Some(system.scheduler.scheduleOnce(period, self, OnceMore(files,period)))

        case Success(left) =>
          master ! PeriodicCheckResult(left)

        case Failure(failure) =>
          println("exception occured:" +failure.toString)
      }

    case PeriodicCheckStop() =>
      task foreach( t => t.cancel())

  }

}

class InitialCheckActor extends Actor {
  import context.dispatcher

  def receive = {
    case InitialCheck(files) =>
      val master = sender
      Future {
        Utils.generateMap(files)
      }.onComplete {
        case Success(result) =>
          model.WorkerDataStorage.workerCRCMaps = result
          master ! InitialCheckOk()
        case Failure(failure)=>
          master ! FSScanError()
      }

  }

}

class FileSystemActor(root: String) extends Actor {
  import context.dispatcher

  def receive = {
    case RequestFS() => {
      val master = sender
      Future {
        Utils.getFileSystem(root)
      }.onComplete {
        case Success(list) =>
          master ! FS(list)
        case Failure(exception) =>
          master ! FSScanError
          println(exception)
      }
    }
  }

}