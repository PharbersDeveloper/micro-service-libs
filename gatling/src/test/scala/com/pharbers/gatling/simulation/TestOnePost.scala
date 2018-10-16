package com.pharbers.gatling.simulation

import io.gatling.core.scenario.Simulation
import com.pharbers.gatling.scenario._

import scala.concurrent.duration._
import com.pharbers.gatling.base.phHttpProtocol
import io.gatling.core.Predef.{rampUsers, scenario}


class TestOnePost extends Simulation{
    import com.pharbers.gatling.base.phHttpProtocol.{noneBlackList,noneWhiteList}

    implicit val token = "5bc56fd7bbac5300014a9a8e"
    val name = "findMedSales"

    val httpProtocol = phHttpProtocol("http://123.56.179.133:7434")

//    val httpProtocol = phHttpProtocol("http://127.0.0.1:9000")

    val scn = scenario(name).exec(TestOnePost.run(name).pause(2 seconds))

    setUp(scn.inject(rampUsers(50) over(10 seconds))).protocols(httpProtocol)

}
