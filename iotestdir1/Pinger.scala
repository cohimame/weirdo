package pinger

import actorcore._
import com.typesafe.config.ConfigFactory
import akka.actor.{ActorSystem, Props, Actor}//, Inbox} only in akka2.2 
import scala.concurrent.duration._
import akka.actor.{ 
  
  ActorInitializationException, 
  ActorKilledException,
  OneForOneStrategy 
}
import akka.actor.SupervisorStrategy._
import akka.actor.ActorContext

class PingerSuperV extends Actor {
  override val supervisorStrategy = 
    OneForOneStrategy() {
      case _:ActorInitializationException => { 
        println("supervisor caught remote actor deploy failure")
        Stop
      }
    }

  val pingerActor = context.actorOf(Props[Pinger], "pinger")

//  val pongerActor = context.actorOf(
//    "akka://pongersystem@localhost:2552/user/ponger")

  def receive = {
    case _ => println("supervisor")
  }


}

object PingerApp extends App {
  val system = ActorSystem(
    "pingersystem", 
    ConfigFactory.load.getConfig("pinger-remote"))

  //actorOf creates if not already existing
 // val pingerActor = system.actorOf(Props[Pinger], "pinger")

  //actorFor checks for existing actor
  val pongerActor = system.actorFor(
    "akka://pongersystem@localhost:2552/user/ponger")

  

//  println("pinger_path=" + pingerActor.path)
//  println("remote_ponger_path=" + pongerActor.path)

  val superV = system.actorOf(Props[Pinger], "pinger-visor")

  superV.pingerActor.!(Ping(12))(pongerActor)

/*

  //val inbox = Inbox.create(system) 
  println(
    system.settings.config.getString(
      "pinger-remote.ponger.ponger-system")
    )
*/


  //system.shutdown()

}