package org.bescala.akkanetworkping

import akka.actor.{Actor, ActorLogging, Props, ActorRef}

object PingServer {
  case class Response(sequenceNumber: Int)

  def props(): Props = Props(new PingServer)

}

class PingServer extends Actor with ActorLogging {

  import PingServer._
  import Pinger._
  
  val reactingToPings: Receive = {
    case Ping(sequenceNumber) => {
      sender ! Response(sequenceNumber)
    }
  }

  override def receive: Receive = reactingToPings
    
}
