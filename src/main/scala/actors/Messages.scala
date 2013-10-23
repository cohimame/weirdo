package actors

import akka.actor.ActorRef
import scala.concurrent.duration.FiniteDuration

object Messages {
  case class PushRequestFS(remoteWorker: ActorRef)
  case class PushRequestIC(remoteWorker: ActorRef,files: List[String])
  case class PushRequestPC(remoteWorker: ActorRef,files: List[String], period: FiniteDuration)
  case class PushRequestPCStop(worker: ActorRef)

  case class RequestFS()
  case class FS(filesystem: List[String])
  case class FSScanError()

  case class InitialCheck(files: List[String])
  case class InitialCheckOk()

  case class PeriodicCheck(files: List[String], period: FiniteDuration)
  case class PeriodicCheckSuccess(result: Boolean)
  case class PeriodicCheckFailure(result: List[String])

  case class PeriodicCheckStop()
}
