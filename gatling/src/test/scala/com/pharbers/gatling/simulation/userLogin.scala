package com.pharbers.gatling.simulation

import io.gatling.core.Predef._

import scala.concurrent.duration._
import com.pharbers.gatling.scenario._
import com.pharbers.gatling.base.phHttpProtocol
import io.gatling.core.structure.ScenarioBuilder

import scala.language.postfixOps

class userLogin extends Simulation {
	import com.pharbers.gatling.base.phHttpProtocol.{noneBlackList, noneWhiteList}

	val httpProtocol = phHttpProtocol("http://127.0.0.1:9000")

	val scn: ScenarioBuilder = scenario("login")
		.exec(
			userLogin.login.pause(5 seconds)
		)

	setUp(scn.inject(rampUsers(100) over (3 seconds))).protocols(httpProtocol)

}
