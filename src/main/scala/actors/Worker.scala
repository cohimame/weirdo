package actors

import akka.actor.{Actor,ActorSystem,Props}
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._

import model.FSStorage._
import actors.Messages._

object Worker extends App {

  val system = ActorSystem("system", ConfigFactory.load.getConfig("prototype"))
  import system.dispatcher

  val fsRequester = system.actorOf(Props[FileSystemRequester],"fsRequester")
  val iCheckRequester = system.actorOf(Props[InitialCheckRequester],"iCheckRequester")

  val worker1_fs = system.actorOf(Props[FileSystemActor], "worker1_fs")
  val worker1_ic = system.actorOf(Props[InitialCheckActor], "worker1_ic")


  fsRequester ! PushRequestFS(worker1_fs)
  system.scheduler.scheduleOnce( 2 seconds ){
    println(workerFileSystem)
    //println(workerFileSystem.get(worker1_fs.path.toString))
  }

  system.scheduler.scheduleOnce( 3 seconds ){
    iCheckRequester ! PushRequestIC(
      worker1_ic,
      workerFileSystem.get(worker1_fs.path.toString).get)
  }

  system.scheduler.scheduleOnce( 6 seconds ){
    println(workerCRCMaps)
    //println(workerFileSystem.get(worker1_fs.path.toString))
  }
  /**/

  system.scheduler.scheduleOnce( 12 seconds ){ system.shutdown() }
}

/*
object Worker extends App {
  val system = ActorSystem("system", ConfigFactory.load.getConfig("prototype"))

  import system.dispatcher

  val worker1 = system.actorOf(Props[FileSystemActor],"worker1")
  val worker2 = system.actorOf(Props[FileSystemActor],"worker2")
  val worker3 = system.actorOf(Props[FileSystemActor],"worker3")
  val admin = system.actorOf(Props[FileSystemRequester],"admin")

  println(model.FSStorage.workerFileSystem)

  admin !  actors.Messages.PushRequestFS(worker1)
  admin !  actors.Messages.PushRequestFS(worker2)
  admin !  actors.Messages.PushRequestFS(worker3)

  system.scheduler.scheduleOnce( 4 seconds ){  println(model.FSStorage.workerFileSystem) }

  system.scheduler.scheduleOnce( 5 seconds ){ system.shutdown() }
}

*/
