package com.pharbers.gatling.simulation

import io.gatling.core.scenario.Simulation
import com.pharbers.gatling.scenario._

import scala.concurrent.duration._
import com.pharbers.gatling.base.phHttpProtocol
import io.gatling.core.Predef._


class TestOnePost extends Simulation{
    import com.pharbers.gatling.base.phHttpProtocol.{noneBlackList,noneWhiteList}

    implicit val token = "5bc69d1e2239130001244e18"
    val name = "findReportMedSales"

    val httpProtocol = phHttpProtocol("http://123.56.179.133:7434")

//    val httpProtocol = phHttpProtocol("http://127.0.0.1:9000")

    val scn = scenario(name).exec(TestOnePost.run(name).pause(2 seconds))

//    setUp(scn.inject(rampUsers(200) over(10 seconds))).protocols(httpProtocol)
    setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)

}
