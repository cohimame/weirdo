package admin

import akka.actor.Actor
import actors.Messages._

class AdminActor extends Actor
  with FileSystemRequester
  with InitialCheckRequester
  with PeriodicCheckRequester {

    def receive = fsRequest orElse initialCheck orElse periodicCheck
}

trait FileSystemRequester { self:Actor =>

  def fsRequest: Receive = {
    case PushRequestFS(worker) =>
      worker ! RequestFS()

    case FS(list) =>
      model.AdminDataStorage.workerFileSystem += (sender.path -> list)

    case FSScanError =>
      println(sender.path.toString + " meet an error while scanning his filesystem ")

  }

}

trait InitialCheckRequester { self: Actor =>

  def initialCheck: Receive = {
    case PushRequestIC(worker,files) =>
      worker ! InitialCheck(files)

    case InitialCheckOk() =>
      println("initial check done")
  }

}

trait PeriodicCheckRequester { self: Actor =>

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