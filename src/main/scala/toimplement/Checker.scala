package scheduling


object ToBeImplemented {

}

/*
import java.io.File

import akka.actor.{Cancellable, Props, Actor}
import akka.pattern.ask

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Success, Failure}

case class InitialCheck(files: List[String])
case class InitialCheckOk(result: Map[String, Long])

case class PeriodicCheck(files: List[String], dur: FiniteDuration)
case class PeriodicCheckResult(success: Boolean)
case class PeriodicCheckUpdate(files: List[String])
case class PeriodicCheckStop()
case class StopSuccess()

class CheckerActor extends Actor {
  var InitialCheckMap = Map[String,Long]()
  var periodicalCheckList = List[String]()
  var duration: FiniteDuration = 0 seconds

  val initialChecker = context.actorOf(Props[InitialCheckActor])
  //var periodicalChecker = context.system.deadLetters
  val periodicalChecker = context.actorOf(Props[PeriodicalCheckActor])

  /*
    1) deadletters => будем убивать актера и делать нового при каждом удобном
    2) неубиваемый => нужно делать Await {PeriodicCheckStop() }.onComplete{новое задание}
  */

  def receive = {

    case iCheck @ InitialCheck(files) => {
      initialChecker forward iCheck
      periodicalChecker ! PeriodicCheckStop()
    }

    case pCheck @ PeriodicCheck(files,dur) => {
      periodicalCheckList = files
      duration = dur
      periodicalChecker forward pCheck
    }

    case PeriodicCheckUpdate(files) => {
      val s = sender
      periodicalCheckList ++ files
      ask(periodicalChecker, PeriodicCheckStop()).onComplete {
        case Success(_) => periodicalChecker.tell(PeriodicCheck(files,duration),s)
        case Failure(ex) => println("") //TODO
      }
    }

    case PeriodicCheckResult(succ) => {
      //TODO
    }

    case InitialCheckOk(result) => {
      InitialCheckMap = result
    }

  }
}


class InitialCheckActor extends Actor {
  import context.dispatcher

  def receive = {
    case InitialCheck(files) =>
      Future {
        Map[String, Long]()/*some io*/
      }.onComplete {
        case Success(result) =>
          context.parent ! InitialCheckOk(result)
        case Failure(failure)=>
          println("faild")
          /* shoulda be an exception for parent-supervisor plus logging*/
      }
  }

}

class PeriodicalCheckActor extends Actor {
  import context.{dispatcher,system}
  var k: Option[Cancellable] = None

  def receive = {
    case PeriodicCheck(files,dur) =>
      Future {
        /*some io + Map compare*/
        true
      }.onComplete {
        case Success(result) => {
          context.parent ! PeriodicCheckResult(result)
          k = Some(system.scheduler.scheduleOnce(dur, self, PeriodicCheck(files,dur)))
        }
        case Failure(failure) =>
          println("faild")/* shoulda be an exception for parent-supervisor plus logging*/
      }

    case PeriodicCheckStop() =>
      val s = sender
      Future { k.foreach(_.cancel()) }.onComplete( _ => s ! StopSuccess() )
  }
}

*/

