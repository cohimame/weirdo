package actors

import akka.actor.{Actor,ActorSystem,Props}
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._

object Worker extends App {
  val system = ActorSystem("system", ConfigFactory.load.getConfig("prototype"))

  import system.dispatcher

  val worker1 = system.actorOf(Props[FileSystemProvider],"worker1")
  val worker2 = system.actorOf(Props[FileSystemProvider],"worker2")
  val worker3 = system.actorOf(Props[FileSystemProvider],"worker3")
  val admin = system.actorOf(Props[FileSystemRequester],"admin")

  println(model.FSStorage.workerFileSystem)

  admin !  actors.Messages.PushRequestFS(worker1)
  admin !  actors.Messages.PushRequestFS(worker2)
  admin !  actors.Messages.PushRequestFS(worker3)

  system.scheduler.scheduleOnce( 7 seconds ){  println(model.FSStorage.workerFileSystem) }

  system.scheduler.scheduleOnce( 10 seconds ){ system.shutdown() }
}


