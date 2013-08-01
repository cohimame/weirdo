package actors.admin

import akka.actor.Actor
import actors.Messages._
import model.AdminDataStorage


class AdminActor extends Actor
  with FileSystemRequester
  with InitialCheckRequester
  with PeriodicCheckRequester {

    def receive = fsRequest orElse initialCheck orElse periodicCheck
}

trait FileSystemRequester { this:Actor =>

  def fsRequest: Receive = {
    case PushRequestFS(worker) =>
      worker ! RequestFS()

    case FS(list) =>
      //println(sender +":" + list.mkString("\n"))
      AdminDataStorage.putWorkerFS(sender,list)

    case FSScanError =>
      println(sender.path.toString + " meet an error while scanning his filesystem ")

  }

}

trait InitialCheckRequester { this: Actor =>

  def initialCheck: Receive = {
    case PushRequestIC(worker,files) =>
      worker ! InitialCheck(files)

    case InitialCheckOk() =>
      println("initial check done")
  }

}

trait PeriodicCheckRequester { this: Actor =>

  def periodicCheck: Receive = {
    case PushRequestPC(worker,files,period) =>
      worker ! PeriodicCheck(files,period)

    case PushRequestPCStop(worker) =>
      worker ! PeriodicCheckStop()

    case PeriodicCheckResult(result) =>
      result match {
        case Left(left) => println(left.mkString("\n"))
        case Right(_) => println("success!")
      }
  }
}
