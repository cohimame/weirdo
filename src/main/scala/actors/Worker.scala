package actors

import akka.actor.{Actor,ActorSystem,Props}
import scala.concurrent.duration._
import actors.Messages._

object Worker extends App {
  val system = ActorSystem("system")
  import system.dispatcher

  object Admin {
    val fsRequester = system.actorOf(Props[FileSystemRequester],"fsRequester")
    val iCheckRequester = system.actorOf(Props[InitialCheckRequester],"iCheckRequester")
    val pCheckRequester = system.actorOf(Props[PeriodicCheckRequester],"pCheckRequester")
  }

  object Worker1 {
    val worker1_fs = system.actorOf(Props[FileSystemActor], "worker1_fs")
    val worker1_ic = system.actorOf(Props[InitialCheckActor], "worker1_ic")
    val worker1_pc = system.actorOf(Props[PeriodicalCheckActor], "worker1_pc")
  }

  Admin.fsRequester ! PushRequestFS(Worker1.worker1_fs)

  system.scheduler.scheduleOnce( 2 seconds ){
    println(model.DataStorage.workerFileSystem)
  }

  system.scheduler.scheduleOnce( 3 seconds ){
    val slice = model.DataStorage.workerFileSystem.slice(2,6)
    Admin.iCheckRequester ! PushRequestIC(Worker1.worker1_ic,slice)
  }



  /*
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

  */

  system.scheduler.scheduleOnce( 12 seconds ){ system.shutdown() }
}
