package com.pharbers.gatling.simulation

import com.pharbers.gatling.base.phHeaders.phToken
import io.gatling.core.scenario.Simulation
import com.pharbers.gatling.scenario._

import scala.concurrent.duration._
import com.pharbers.gatling.base.phHttpProtocol
import io.gatling.core.Predef._

import scala.language.postfixOps


class TestOnePost extends Simulation{
    import com.pharbers.gatling.base.phHttpProtocol.{noneBlackList,noneWhiteList}

    implicit val implToken = phToken("5bc58327c8f5e406a2b57394")
    val name = "findAllMedUnit"

    val httpProtocol = phHttpProtocol("http://www.pharbers.com:443")
//    val httpProtocol = phHttpProtocol("http://123.56.179.133:18024") // apmCalc
//    val httpProtocol = phHttpProtocol("http://127.0.0.1:9000")

    val scn = scenario(name).exec(TestOnePost.run(name)) //.pause(10 seconds))

//    setUp(scn.inject(rampUsers(200) over(5 seconds))).protocols(httpProtocol)
    setUp(scn.inject(atOnceUsers(300))).protocols(httpProtocol)
}