package com.pharbers.gatling.simulation

import io.gatling.core.scenario.Simulation
import com.pharbers.gatling.scenario._

import scala.concurrent.duration._
import com.pharbers.gatling.base.phHttpProtocol
import io.gatling.core.Predef.{rampUsers, scenario}


class TestOnePost extends Simulation{
    import com.pharbers.gatling.base.phHttpProtocol.{noneBlackList,noneWhiteList}

    val name = "exam"

//    val httpProtocol = phHttpProtocol("http://123.56.179.133:7434")

    val httpProtocol = phHttpProtocol("http://127.0.0.1:9000")

    val scn = scenario(name).exec(TestOnePost.run(name).pause(5 seconds))

    setUp(scn.inject(rampUsers(1) over(10 seconds))).protocols(httpProtocol)

}
