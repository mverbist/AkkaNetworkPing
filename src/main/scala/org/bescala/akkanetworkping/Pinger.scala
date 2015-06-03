package org.bescala.akkanetworkping

import akka.actor.{ActorLogging, Actor, Props, ActorRef}
import scala.concurrent.duration._
import scala.language.postfixOps
import akka.actor.Cancellable

object Pinger {
  case class Ping(sequenceNumber: Int)

  def props(pingServer: ActorRef, pingCount: Int, pingInterval: Int): Props = Props(new Pinger(pingServer, pingCount, pingInterval))

}

class Pinger(pingServer: ActorRef, pingCount: Int, pingInterval: Int) extends Actor with ActorLogging {

  import Pinger._
  import PingServer._
  import context.dispatcher
  
  var _seqCounter = 0
  def nextSeq = {
    val ret = _seqCounter
    _seqCounter += 1
    ret
  }
  
  val thisPingerActor = self
  val a: Cancellable = context.system.scheduler.schedule(0 seconds, pingInterval seconds) {
    val pingMessage = Ping(nextSeq)
    log.info(s"Sending $pingMessage")
    pingServer ! pingMessage
    if (_seqCounter == pingCount) {
      a.cancel()
    	context.stop(thisPingerActor)      
    }
  }
  
  val listeningToResponses: Receive = {
    case Response(sequenceNumber) => {
      log.info(s"Received ping response $sequenceNumber")
    }
  }

  override def receive: Receive = listeningToResponses

}
