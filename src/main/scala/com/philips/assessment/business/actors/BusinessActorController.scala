package com.philips.assessment.business.actors

import akka.actor.Actor
import com.philips.assessment.business.actors.BusinessActorController.{DoBusinessStuff, StuffDone}


object BusinessActorController{
    trait BusinessMessage
    case object DoBusinessStuff extends BusinessMessage
    case class StuffDone(stuff : String) extends BusinessMessage
}

class BusinessActorController extends Actor{

  override def receive : Receive = {
    case DoBusinessStuff => sender ! StuffDone(s"cool stuff done from $self to $sender!")
  }
}
