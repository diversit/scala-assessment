package com.philips.assessment.endpoint.modules

import akka.actor.{ActorRef, ActorSystem, Props}
import com.google.inject.{Provides, Singleton}
import com.philips.assessment.endpoint.actors.EndpointActorController
import com.philips.assessment.utils.SimpleBusinessActorSelector
import com.twitter.inject.TwitterModule


object ClusterTestModule extends TwitterModule{

  override val modules = Seq(ClusterSystemTestModule)

  @Singleton
  @Provides
  def providesEndpointActor(system: ActorSystem): ActorRef = {

    class MyEndpoint extends EndpointActorController with SimpleBusinessActorSelector

    system.actorOf(Props[MyEndpoint], name = "endpoint")
  }

}

object ClusterSystemTestModule extends TwitterModule {

  @Singleton
  @Provides
  def providesSystem: ActorSystem = {

    ActorSystem("SimpleSystem")
  }
}
