package com.philips.assessment.utils.actors

import akka.actor.{Actor, ActorSelection, RootActorPath}
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberEvent}
import akka.cluster.Member
import com.philips.assessment.utils.ClusterSupport


trait ClusterState[T] {
  def getClusterState :T
}

trait ClusterStateByNodeRole extends ClusterState[Map[String, List[ActorSelection]]] {
  this: Actor  with ClusterSupport=>

  protected var clusterMembers = Map[String, List[ActorSelection]]() //(Role -> Members in the cluster with that role)

  override def getClusterState = clusterMembers

  override def onInitialState(state: CurrentClusterState) =  state.members.foreach(addMember)

  override def onMemberUp(member: Member) = addMember(member)

  override def onNonMemberUp(event: MemberEvent) = removeMember(event.member)

  override def onUnreachableMember(member: Member) = removeMember(member)

  override def onReachableMember(member: Member) = addMember(member)

  private def addMember(member: Member): Unit = {
    if (!member.roles.isEmpty) {
      // Convention: (head) role is used to label the nodes (single) actor
      val memberName = member.roles.head

      for (rol: String <- member.roles) {
        val actor = context.actorSelection(RootActorPath(member.address) / "user" / memberName)
        if (clusterMembers.contains(rol)) {
          clusterMembers = clusterMembers + (rol -> (actor :: clusterMembers(rol)))
        } else {
          clusterMembers = clusterMembers + (rol -> List(actor))
        }
      }
      println(s"\n\nAdded Node. New state of cluster Members: $clusterMembers \n\n")
    }
  }

  private def removeMember(member: Member): Unit = {
    if (!member.roles.isEmpty) {
      // Convention: (head) role is used to label the nodes (single) actor
      val memberPath = RootActorPath(member.address)

      for (rol: String <- member.roles) {

        if (clusterMembers.contains(rol)) {
          val newMembers = clusterMembers(rol).filter {
            _.anchorPath.compareTo(memberPath) != 0
          }
          clusterMembers = clusterMembers + (rol -> newMembers)
        }
      }
    }
    println(s"\n\nRemoved Node. New state of cluster Members: $clusterMembers \n\n")
  }
}
