package com.pharbers.gatling.simulation

import io.gatling.core.Predef._
import scala.concurrent.duration._

import com.pharbers.gatling.scenario.getHome
import com.pharbers.gatling.base.phHttpProtocol

class getHome extends Simulation {
	import com.pharbers.gatling.base.phHttpProtocol.{noneBlackList, noneWhiteList}

	val httpProtocol = phHttpProtocol("http://123.56.179.133:7434")

	val scn = scenario("max_home")
		.exec(getHome.getHome)
		.pause(5)

	setUp(scn.inject(rampUsers(50) over (10 seconds))).protocols(httpProtocol)
}
