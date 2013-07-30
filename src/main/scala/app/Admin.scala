package app

import actors.Messages
import Messages._
import akka.actor.{Actor,ActorSystem,Props}
import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory
import admin.AdminActor

/**/
object Admin {
  def main(args: Array[String]) {
    val system = ActorSystem("admin-system", ConfigFactory.load.getConfig("Admin"))

    val admin = system.actorOf(Props[AdminActor], "admin")
    val worker = system.actorFor("akka://worker-system@localhost:3001/user/Worker1")

    println(admin.path)


    import system.dispatcher

    //system.scheduler.scheduleOnce( 5 seconds ){ admin ! PushRequestFS(worker) }


    system.scheduler.scheduleOnce( 20 seconds ){ system.shutdown() }
  }
}



/*

object Admin {

    def main(args: Array[String]) {

    val system = ActorSystem("admin-system", ConfigFactory.load.getConfig("Admin"))
    val admin = system.actorOf(Props[AdminActor], "admin")


    val worker1 = system.actorFor("akka://worker-system@localhost:3001/user/Worker1")
      /*
    val worker2 = system.actorFor("akka://worker-system@localhost:3002/user/Worker2")
    val worker3 = system.actorFor("akka://worker-system@localhost:3003/user/Worker3")
      */

    import system.dispatcher


    admin ! PushRequestFS(worker1)

      /*
      admin ! PushRequestFS(worker2)
      admin ! PushRequestFS(worker3)
      */

    system.scheduler.scheduleOnce( 4 seconds ){
      val fS = model.AdminDataStorage.workerFileSystem
      fS.foreach( (record) => println(record._1 + "" + record._2.mkString("\n"))  )
    }


    system.scheduler.scheduleOnce( 20 seconds ){
      val whole = model.AdminDataStorage.workerFileSystem
      /*
        admin ! PushRequestIC(worker1, whole.get(worker1.path).get)
        admin ! PushRequestIC(worker2, whole.get(worker2.path).get)
        admin ! PushRequestIC(worker3, whole.get(worker3.path).get)
      */
    }


    system.scheduler.scheduleOnce( 120 seconds ){ system.shutdown() }
    }


}

*/

/*

object Worker extends App {
  val system = ActorSystem("system")
  import system.dispatcher

  object Admin {
    val fsRequester = system.actorOf(Props[FileSystemRequester],"fsRequester")
    val iCheckRequester = system.actorOf(Props[InitialCheckRequester],"iCheckRequester")
    val pCheckRequester = system.actorOf(Props[PeriodicCheckRequester],"pCheckRequester")
  }

  object Worker {
    val worker1_fs = system.actorOf(Props[FileSystemActor], "worker1_fs")
    val worker1_ic = system.actorOf(Props[InitialCheckActor], "worker1_ic")
    val worker1_pc = system.actorOf(Props[PeriodicalCheckActor], "worker1_pc")
  }


  // Acquire filesystem list
  Admin.fsRequester ! PushRequestFS(Worker.worker1_fs)

  system.scheduler.scheduleOnce( 2 seconds ){
    println(model.AdminDataStorage.workerFileSystem)
  }

  // Ask actors.worker for initial check
  system.scheduler.scheduleOnce( 3 seconds ){
    val whole = model.AdminDataStorage.workerFileSystem
    Admin.iCheckRequester ! PushRequestIC(Worker.worker1_ic,whole)
  }


  system.scheduler.scheduleOnce( 6 seconds ){
    println(model.AdminDataStorage.workerCRCMaps)
  }

  system.scheduler.scheduleOnce(7 seconds){
    val slice = model.AdminDataStorage.workerFileSystem//.slice(0,5)

    Admin.pCheckRequester ! PushRequestPC(Worker.worker1_pc, slice, 20 seconds)
  }


  system.scheduler.scheduleOnce( 120 seconds ){ system.shutdown() }
}


*/