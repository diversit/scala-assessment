package com.philips.assessment.business

import akka.actor.{ActorSystem, Props}
import com.philips.assessment.business.actors.BusinessActorController
import com.philips.assessment.utils.ClusterSupport
import com.typesafe.config.ConfigFactory

/**
  * Created by roberto on 17/09/2016.
  */
object BusinessLauncher {

  def main(args: Array[String]): Unit = {

    if (!args.isEmpty) {
      try {
        val List(port) = args.toList
        run(port.toInt)
      }
      catch {
        case e: Throwable =>
          e.printStackTrace()
          printHelp
      }
    } else {
      run(2551)
      run(2552)
    }
  }

  def printHelp = println(
    """
      | - endpoint [serverPort,actorPort]
      | - business [actorPort]
    """.stripMargin
  )

  def run (actorPort : Int) = {
    val clusterConfiguration =
      ConfigFactory.parseString(s"akka.remote.netty.tcp.port=" + actorPort)
        .withFallback(ConfigFactory.parseString("akka.cluster.roles = [business]"))
        .withFallback(ConfigFactory.load())

    val system = ActorSystem("ClusterSystem", clusterConfiguration)

    class BusinessNode extends BusinessActorController with ClusterSupport

    system.actorOf(Props[BusinessNode], name = "business")
  }

}
