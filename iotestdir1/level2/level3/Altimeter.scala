package avio;

import akka.actor.{ Props, Actor, ActorSystem, ActorLogging }

import akka.testkit.{TestKit, TestActorRef, ImplicitSender}

import org.scalatest.matchers.MustMatchers
import org.scalatest.{WordSpec, BeforeAndAfter}


import akka.util.Duration
import akka.dispatch.ExecutionContext

import java.util.concurrent.TimeUnit


object Altimeter {
  case class RateChange(amount: Float)
  case class AltitudeUpdate(altitude: Double)
  
  def apply() = new Altimeter with ProductionEventSource
}

class Altimeter extends Actor with ActorLogging  { 
  this: EventSource =>
  
  import Altimeter._

  val ceiling = 43000
  val maxRateOfClimb = 5000
  
  var rateOfClimb: Float = 0
  var altitude: Double = 0
  var lastTick = System.currentTimeMillis

  val ticker = context.system.scheduler.schedule(
     Duration.create(100, TimeUnit.MILLISECONDS),
     Duration.create(100, TimeUnit.MILLISECONDS),
     self,
     Tick)

  case object Tick

  
  def altimeterReceive: Receive = {
    case RateChange(amount) =>
      rateOfClimb = amount.min(1.0f).max(-1.0f) * maxRateOfClimb
      log.info("Altimeter changed rate of climb to" + rateOfClimb)

    case Tick =>
      val tick = System.currentTimeMillis
      altitude = altitude + ((tick - lastTick) / 60000.0) * rateOfClimb
      lastTick = tick
      sendEvent(AltitudeUpdate(altitude))
  }
  
  def receive = eventSourceReceive orElse altimeterReceive 

  override def postStop(): Unit = ticker.cancel
}

