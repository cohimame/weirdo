import actors.{PeriodicalCheckActor, PeriodicCheckRequester, InitialCheckRequester, FileSystemRequester}
import akka.actor.{Actor,ActorSystem,Props}
import scala.concurrent.duration._
import actors.Messages._




/*

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


  // Acquire filesystem list
  Admin.fsRequester ! PushRequestFS(Worker1.worker1_fs)

  system.scheduler.scheduleOnce( 2 seconds ){
    println(model.DataStorage.workerFileSystem)
  }

  // Ask worker for initial check
  system.scheduler.scheduleOnce( 3 seconds ){
    val whole = model.DataStorage.workerFileSystem
    Admin.iCheckRequester ! PushRequestIC(Worker1.worker1_ic,whole)
  }


  system.scheduler.scheduleOnce( 6 seconds ){
    println(model.DataStorage.workerCRCMaps)
  }

  system.scheduler.scheduleOnce(7 seconds){
    val slice = model.DataStorage.workerFileSystem//.slice(0,5)

    Admin.pCheckRequester ! PushRequestPC(Worker1.worker1_pc, slice, 20 seconds)
  }


  system.scheduler.scheduleOnce( 120 seconds ){ system.shutdown() }
}


*/