package org.bescala.akkanetworkping

import akka.actor.{ActorLogging, Actor, Props, ActorRef}

object Pinger {
  case class Ping(sequenceNumber: Int)

  def props(pingServer: ActorRef, pingCount: Int, pingInterval: Int): Props = Props(new Pinger(pingServer, pingCount, pingInterval))

}

class Pinger(pingServer: ActorRef, pingCount: Int, pingInterval: Int) extends Actor with ActorLogging {

  import Pinger._
  import PingServer._
  
  var _seqCounter = 0
  def nextSeq = {
    val ret = _seqCounter
    _seqCounter += 1
    ret
  }

  for (i <- 1 to pingCount) {
    val pingMessage = Ping(nextSeq)
    log.info(s"Sending $pingMessage")
    pingServer ! pingMessage      
  }
  
  val listeningToResponses: Receive = {
    case Response(sequenceNumber) => {
      log.info(s"Received ping response $sequenceNumber")
    }
  }

  override def receive: Receive = listeningToResponses

}
