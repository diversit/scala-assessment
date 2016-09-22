package com.philips.assessment.utils

import akka.actor.{Actor, ActorSelection, Props}
import com.philips.assessment.business.actors.BusinessActorController
import com.philips.assessment.utils.actors.ActorSelector

trait SimpleBusinessActorSelector extends ActorSelector {
  this: Actor =>

  val actorRef = Some(context.actorOf(Props[BusinessActorController]))

  override def selectActorPath(): Option[ActorSelection] = Some(context.actorSelection(actorRef.get.path))
}
