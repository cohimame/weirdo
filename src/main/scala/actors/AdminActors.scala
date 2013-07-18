package actors

import akka.actor.Actor

class AdminActor extends Actor
  with FileSystemRequester
  with InitialCheckRequester
  with PeriodicCheckRequester {

    def receive = fsRequest orElse initialCheck orElse periodicCheck

}

trait FileSystemRequester { self:Actor =>
  import Messages._

  def fsRequest: Receive = {
    case PushRequestFS(worker) =>
      worker ! RequestFS()

    case FS(list) =>
      model.DataStorage.workerFileSystem = list

    case FSScanError =>
      println(sender.path.toString + " meet an error while scanning his filesystem ")

  }

}

trait InitialCheckRequester { self: Actor =>
  import Messages._

  def initialCheck: Receive = {
    case PushRequestIC(worker,files) =>
      worker ! InitialCheck(files)

    case InitialCheckOk() =>
      println("initial check done")
  }

}

trait PeriodicCheckRequester { self: Actor =>
  import Messages._

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
