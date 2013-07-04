package iteration.first

import akka.actor.Actor
import scala.concurrent.Future
import scala.util.{Failure, Success}

object Messages {
  case class Start()
  case class FS(filesystem: List[String])
  case class Error()
}

class A1 extends Actor {
  import Messages._
  import context.dispatcher

  def receive = {
    case Start() => {
      val master = sender
      Future {
        Utils.getFileSystem()
      }.onComplete {
        case Success(list) =>
          master ! FS(list)
        case Failure(exception) =>
          master ! Error
          println(exception)
      }
    }
  }

}

object Worker extends App {
  import akka.actor.{ActorSystem,Props}
  import com.typesafe.config.ConfigFactory
  import scala.concurrent.duration._


  val system = ActorSystem("system", ConfigFactory.load.getConfig("prototype"))

  import system.dispatcher


  val fSActor = system.actorOf(Props[A1])
  val resultPrint = system.actorOf(Props(
    new Actor {
      def receive = {
        case Messages.FS(list) => println(list.mkString("\n"))
      }
    }
  ))

  fSActor.tell(Messages.Start(), resultPrint)

  system.scheduler.scheduleOnce( 4 seconds ){ system.shutdown() }
}


