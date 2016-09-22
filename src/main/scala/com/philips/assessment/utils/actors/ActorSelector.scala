package com.philips.assessment.utils.actors

import akka.actor.{Actor, ActorSelection}
import com.philips.assessment.utils.ClusterSupport

import scala.util.Random


/**
  * Created by roberto on 20/09/2016.
  */
trait ActorSelector {

  def selectActorPath(): Option[ActorSelection]
}

trait RandomClusterSelector extends ActorSelector with ClusterSupport {

  this: Actor with ClusterState[Map[String, List[ActorSelection]]] =>

  override def selectActorPath(): Option[ActorSelection] = {

    val businessActors = getClusterState.getOrElse("business", List.empty)

    if (businessActors.size == 0) {
      None
    } else {
      Some(businessActors(Random.nextInt(businessActors.size)))
    }
  }
}
