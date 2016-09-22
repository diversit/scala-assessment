package com.philips.assessment.utils

import akka.actor.Actor
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, Member}

/**
  * Configure this actor as a cluster node and listening for membership.
  * It's necessary to bind clusterReceive function to the regular actor receive function to listen
  *
  * Create a clusterMembers : Map[String, List[ActorSelection]] (Role -> Members in the cluster with that role)
  */
trait ClusterSupport {
  this: Actor =>

  val cluster = Cluster(context.system)

  override def preStart() = cluster.subscribe(self, classOf[MemberEvent], classOf[ReachabilityEvent])

  override def postStop() = cluster.unsubscribe(self)

  /**
    * Bind to [[akka.actor.Actor.receive]]
    * @return
    */
  protected def clusterReceive: Receive = {
    case state : CurrentClusterState => onInitialState(state)
    case MemberUp(m) => onMemberUp(m)
    case other: MemberEvent => onNonMemberUp(other)
    case UnreachableMember(m) => onUnreachableMember(m)
    case ReachableMember(m) => onReachableMember(m)
  }

  protected def onInitialState(state : CurrentClusterState) = {}

  protected def onMemberUp(member: Member) = {}

  protected def onNonMemberUp(event: MemberEvent) = {}

  protected def onUnreachableMember(member: Member) = {}

  protected def onReachableMember(member: Member) = {}

}
