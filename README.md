# Assessment

This assessment is used to provide a topic for interviewing a candidate for
a Scala role.

## Case:

In Januari 2015 our team started developing an IoT application using the
libraries Spray and [Akka](http://www.akka.io).  
On year later, because of changed requirements we have to make some major changes
to the application. We use this opportunity to also change the application
because of changed insights and having learned to use these libraries.

In particular, we want to replace Spray with another HTTP framework.
After looking at several alternatives, we choose [Finatra](https://twitter.github.io/finatra/), a Twitter framework based on [Finagle](http://twitter.github.io/finagle/) and [TwitterServer](http://twitter.github.io/twitter-server/).

Even though we selected a new http framework, we would like to reuse as much code of the original [Akka](http://www.akka.io) application as possible.

## Question

* How would you integrate Finatra and Akka ?
* Implement an endpoint which uses an Akka actor.
* Write an integration test for the endpoint using ScalaTest.

## Answer

### Running the app

In order to execute correctly this application it is needed to run 2 diferents main class. 

* Start, at least, two akka nodes in port 2551 and in port 2552 (seed nodes):
```scala
sbt "runMain com.philips.assessment.business.BusinessLauncher 2552"
```

* Start a Finatra endpoint. Port 9999 is used:
```scala
sbt "runMain com.philips.assessment.endpoint.FinatraServerMain"
```

**Run considerations**: SBT is required. If it is not possible to run the app in this 3 ports, go to
`com.philips.assessment.endpoint.finatra.imp.FinatraServerMain` and `scala-assessment\src\main\resources\application.conf` and change the config parameters.

### Explanation

Akka is a JDK libray that allows develope concurrent, distributed, scalable and reliable applications based on actor model. A comon use case is to have a cluster of several nodes in orther to be able of process hight throughtput requirements (IoT for example). Our mission is create a new endpoint to interact with the cluster througth a REST API using Finatra Framework. `BusinessActorController` has been created to simulate a cluster node.   

First step is create `FinatraServer` that handle `MyFinatraEndpoint`. Each `FinatraServer` has associated an Actor, `EndpointActorController`, which will be used to communicate the endpoint with the cluster. For each request, a message is send to `EndpointActorController` using the ask pattern. A future will be generated and will not be complited until a response is received from the cluster.

```scala
def doStuff(action: EndpointMessage, retries: Int = 0): Future[StuffDone] = {
    (actorController ? DoStuff)
      .mapTo[StuffDone]
      ...
  }
```

Hire is a representation of whats happening
![Alt text](/../master/images/endpoint.png?raw=true )
Note that the algoritm used for load balancing messages from EnpointActor and BusinessActor is a simple random balance per node rol. Diferent balances policies can be implemented ussing the information that we receive from ClusterEvents. Also, it is posible to use Router utilitis provides by Akka.

One of the advantages of this implementation is that is easy create new instance of the endpoint if needed. We could implement an architecture similar like as follows just configuring an Apache Server for the balance of the Http requests:
![Alt text](/../master/images/multinodes.png?raw=true )



