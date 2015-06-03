package org.bescala.akkanetworkping

import akka.actor.{Actor, ActorLogging, Props, ActorRef}
import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory
import akka.actor.Cancellable

object PingServer {
  case class Response(sequenceNumber: Int)

  val config = ConfigFactory.load()
  val responseDelay = config.getInt("AkkaNetworkPing.Response.responseDelay")

  def props(): Props = Props(new PingServer(responseDelay seconds))

}

class PingServer(responseDelay: FiniteDuration) extends Actor with ActorLogging {

  import PingServer._
  import Pinger._
  import context.dispatcher

  val reactingToPings: Receive = {
    case Ping(sequenceNumber) => {
      val originalSender = sender
      val a: Cancellable = context.system.scheduler.scheduleOnce(responseDelay) {
      	originalSender ! Response(sequenceNumber)
      }
    }
  }

  override def receive: Receive = reactingToPings
    
}
