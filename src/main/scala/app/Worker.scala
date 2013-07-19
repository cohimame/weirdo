package app

import akka.actor.{Actor,ActorSystem,Props}
import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory
import worker.WorkerActor

object Worker {
  def main(args: Array[String]) {
    val configName: String = args(0)
    val dir: String = args(1)

    val system = ActorSystem("worker-system", ConfigFactory.load.getConfig(configName))
    val worker = system.actorOf(Props{ new WorkerActor(dir)}, configName)

    println(worker.path)

    import system.dispatcher
    system.scheduler.scheduleOnce( 120 seconds ){ system.shutdown() }

  }

}