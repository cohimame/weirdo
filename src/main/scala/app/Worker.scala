package app

import akka.actor.{ActorSystem,Props}
import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory
import actors.admin.WorkerActor

object Worker {
  def main(args: Array[String]) {
    val configName: String = args(0)
    val dir: String = args(1)

    val system = ActorSystem("worker-system", ConfigFactory.load.getConfig(configName))
    val worker = system.actorOf(Props{ new WorkerActor(dir)}, configName)

    import system.dispatcher
    system.scheduler.scheduleOnce( 40 seconds ){ system.shutdown() }

  }

}