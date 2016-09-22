package com.philips.assessment.endpoint.modules

import akka.actor.{ActorRef, ActorSystem, Props}
import com.google.inject.{Provides, Singleton}
import com.philips.assessment.endpoint.actors.EndpointActorController
import com.philips.assessment.utils.ClusterSupport
import com.philips.assessment.utils.actors.{ClusterStateByNodeRole, RandomClusterSelector}
import com.twitter.inject.TwitterModule
import com.typesafe.config.ConfigFactory


object ClusterModule extends TwitterModule {

  override val modules = Seq(ClusterSystemModule)

  @Singleton
  @Provides
  def providesEndpointActor(system: ActorSystem): ActorRef = {

    class EndpointNode extends EndpointActorController with ClusterSupport with ClusterStateByNodeRole with RandomClusterSelector{
      override def receive: Receive = super.receive orElse clusterReceive
    }

    system.actorOf(Props[EndpointNode], name = "endpoint")
  }
}

object ClusterSystemModule extends TwitterModule {


  @Singleton
  @Provides
  def providesSystem: ActorSystem = {
    val clusterConfiguration =
      ConfigFactory.parseString(s"akka.remote.netty.tcp.port=0")
        .withFallback(ConfigFactory.parseString("akka.cluster.roles = [endpoint]"))
        .withFallback(ConfigFactory.load())

    ActorSystem("ClusterSystem", clusterConfiguration)
  }
}
