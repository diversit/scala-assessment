package com.philips.assessment.endpoint.finatra.imp

import akka.actor.{ActorSystem, Props}
import akka.pattern.{AskTimeoutException, ask}
import akka.util.Timeout
import com.philips.assessment.business.actors.BusinessActorController.StuffDone
import com.philips.assessment.endpoint.actors.EndpointActorController.{DoStuff, EndpointMessage}
import com.philips.assessment.endpoint.actors.NewEndpoint
import com.twitter.bijection.Conversion._
import com.twitter.bijection.twitter_util.UtilBijections.twitter2ScalaFuture
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.util.{Future => TwitterFuture}
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}


class MyFinatraEndpoint extends Controller {

  val system = getActorSystem()
  val actorController = system.actorOf(Props[NewEndpoint], name = "endpoint")

  implicit val executor: ExecutionContext = system.dispatcher
  implicit val timeout = new Timeout(1 seconds)



  get("/") { request: Request =>
    doStuff( DoStuff ).as[TwitterFuture[StuffDone]]
  }

  /**
    * Send a message to EndpointActorController in order to do some business logic.
    * If TimeoutException is thrown, will try 3 more times.
    *
    * @param action
    * @param retries
    * @return
    */
  def doStuff(action: EndpointMessage, retries: Int = 0): Future[StuffDone] = {
    (actorController ? DoStuff)
      .mapTo[StuffDone]
      .recoverWith {
        case e : AskTimeoutException if retries < 3 =>
          println("TimeoutException "+ retries)
          doStuff(action, retries + 1)
      }
  }

  def getActorSystem() = {

    val clusterConfiguration =
      ConfigFactory.parseString(s"akka.remote.netty.tcp.port=0")
        .withFallback(ConfigFactory.parseString("akka.cluster.roles = [endpoint]"))
        .withFallback(ConfigFactory.load())

    ActorSystem("ClusterSystem", clusterConfiguration)
  }
}
