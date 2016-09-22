package com.philips.assessment.enpoint.finatra.imp

import com.google.inject.Module
import com.philips.assessment.endpoint.FinatraServer
import com.philips.assessment.endpoint.modules.{ClusterSystemTestModule, ClusterTestModule}
import com.twitter.finagle.http.Status
import com.twitter.finatra.http.test.EmbeddedHttpServer
import com.twitter.inject.server.FeatureTest

/**
  * Created by roberto on 19/09/2016.
  */
class MyFinatraEndpointIntegrationTest extends FeatureTest {

  override val server = new EmbeddedHttpServer(
    twitterServer = new FinatraServer {
      override def overrideModules: Seq[Module] = Seq(ClusterTestModule,ClusterSystemTestModule)
    }
  )


  "MyTest" should  {

    "perform feature" in {
      server.httpGet(
        path = "/",
        andExpect = Status.Ok)
    }
  }

}
