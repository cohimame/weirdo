package actors.admin

import akka.pattern.pipe
import akka.actor.{Props, Actor, ActorRef, Cancellable}
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

import actors.utils.Utils
import actors.Messages._
import model.WorkerDataStorage

class WorkerActor(path: String) extends Actor {
  val fsScanner = context.actorOf(Props{new FileSystemActor(path)})
  val initChecker = context.actorOf(Props[InitialCheckActor])
  val periodicalChecker = context.actorOf(Props[PeriodicalCheckActor])

  def receive = {
    case msg @ RequestFS() =>
      println("WorkerActor got request from" + sender)
      fsScanner forward msg
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
  case class SuccessfulCheck(files:List[String], period: FiniteDuration, rigth: Boolean)
  case class FailedCheck(files:List[String], period: FiniteDuration, left: List[String])
  //case class FutureExecutionFailure((files:List[String], period: FiniteDuration, failure: Failure ))

  def receive = {

    case PeriodicCheck(files, period) =>
      task foreach( t => t.cancel())
      master = sender
      self ! OnceMore(files, period)

    case OnceMore(files, period) => {
      val oldMap = Future( WorkerDataStorage.workerCRCMaps )  
      val currentMap = Future( Utils.generateMap(files) )

      val result = for {
        oM <- oldMap
        cM <- currentMap
      } yield Utils.compareCRCMaps(oM,cM) 

      result map { r => 
        r match {
          case Right(result) => SuccessfulCheck(files,period,result)
          case Left(error) => FailedCheck(files,period,error)
        } 
       } pipeTo self

       /*
          TODO: use Future.fallbackTo() or Future.recover() to handle future 
          execution failure and pipe it.  
          Look, like it was in previous blocking version:

          Future {...}.onComplete {
            case Success(Right(...)) => ...
            case Success(Left(...)) => ...
            case Failure(failure) => ...          
          }
       */ 
    }

    case SuccessfulCheck(files, period, right) =>
      master ! PeriodicCheckSuccess(right)
      task = Some(system.scheduler.scheduleOnce(period, self, OnceMore(files,period)))

    case FailedCheck(files, period, left) =>
      master ! PeriodicCheckFailure(left)

    /* when future fails:    
      case Failure(failure) => 
        println("exception occured:" +failure.toString)
    */  

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
      print("FSactor got request from" + master)
      Future {
        Utils.getFileSystem(root)
      }.onComplete {
        case Success(list) =>
          master ! FS(list)
          //println("sending " + list.mkString("\n") +"to " + master)
        case Failure(exception) =>
          master ! FSScanError
          println(exception)
      }
    }
  }

}
