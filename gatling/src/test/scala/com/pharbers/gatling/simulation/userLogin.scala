package com.pharbers.gatling.simulation

import io.gatling.core.Predef._
import scala.concurrent.duration._

import com.pharbers.gatling.scenario._
import com.pharbers.gatling.base.phHttpProtocol

class userLogin extends Simulation {
	import com.pharbers.gatling.base.phHttpProtocol.{noneBlackList, noneWhiteList}

	val httpProtocol = phHttpProtocol("http://123.56.179.133:18002")

	val scn = scenario("user_login")
		.exec(
			getHome.getHome
					.pause(5 seconds)
//			,userLogin.login
//					.pause(60 seconds)
		)

	setUp(scn.inject(rampUsers(1000) over (3 seconds))).protocols(httpProtocol)

}
