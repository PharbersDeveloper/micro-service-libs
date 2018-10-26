package com.pharbers.gatling.simulation

import io.gatling.core.Predef._
import scala.concurrent.duration._

import com.pharbers.gatling.scenario._
import com.pharbers.gatling.base.phHttpProtocol

class userLogin extends Simulation {
	import com.pharbers.gatling.base.phHttpProtocol.{noneBlackList, noneWhiteList}

	val httpProtocol = phHttpProtocol("http://123.56.179.133:7434")

	val scn = scenario("user_login")
		.exec(
			userLogin.login.pause(2 seconds)
		)

	setUp(scn.inject(rampUsers(100) over (10 seconds))).protocols(httpProtocol)

}
