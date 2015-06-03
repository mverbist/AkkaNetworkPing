package org.bescala.akkanetworkping

import akka.actor.{Props, ActorRef, ActorLogging, Actor}

object PingMaster {

  def props(pingServer: ActorRef): Props = Props(new PingMaster(pingServer))
}

class PingMaster(pingServer: ActorRef) extends Actor with ActorLogging {

  import PingResponseCoordinator._

  var _seqCounter = 0
  def nextSeq = {
    val ret = _seqCounter
    _seqCounter += 1
    ret
  }

  val mastering: Receive = {
    case CreatePinger(pingCount, pingInterval) => {
      log.info(s"Receiving message ${CreatePinger(pingCount, pingInterval)}")
      context.system.actorOf(Pinger.props(pingServer, pingCount, pingInterval), s"Pinger-$nextSeq")
    }
  }

  override def receive: Receive = mastering

}
