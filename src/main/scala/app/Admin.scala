package app

import actors.Messages._
import akka.actor.{ActorSystem, Props}
import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory
import actors.admin.AdminActor
import model.AdminDataStorage

/**/
object Admin {
  def main(args: Array[String]) {
    val system = ActorSystem("admin-system", ConfigFactory.load.getConfig("Admin"))
    import system.dispatcher


    val admin = system.actorOf(Props[AdminActor], "admin")

    val worker1 = system.actorFor("akka://worker-system@localhost:3001/user/Worker1")
    val worker2 = system.actorFor("akka://worker-system@localhost:3002/user/Worker2")
    val worker3 = system.actorFor("akka://worker-system@localhost:3003/user/Worker3")

    val workers = List(worker1,worker2,worker3)


    system.scheduler.scheduleOnce( 3 seconds ) {
      workers foreach { admin ! PushRequestFS(_) }
    }

    system.scheduler.scheduleOnce( 7 seconds ) {
      workers foreach { w =>
        println(w + "\'s filesystem:")
        AdminDataStorage.getWorkerFS(w) foreach {
            f => println(f.mkString("\n"))
        }
      }
    }

    system.scheduler.scheduleOnce( 8 seconds ) {
      workers foreach { w =>
        val fs: Option[List[String]] = AdminDataStorage.getWorkerFS(w)
        fs foreach {
          f => admin ! PushRequestIC(w,f)
        }
      }
    }


    system.scheduler.scheduleOnce( 11 seconds ){
      workers foreach {
        w =>
          AdminDataStorage.getWorkerFS(w) foreach {
            f =>
              val (p1,p2) = f.splitAt(4)
              admin ! PushRequestPC(w,p1 ::: p2, 5 seconds)
        }
      }

    }

    system.scheduler.scheduleOnce( 60 seconds ){ system.shutdown() }

  }


}
