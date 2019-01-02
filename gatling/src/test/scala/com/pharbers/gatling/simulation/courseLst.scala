//package com.pharbers.gatling.simulation
//
//import io.gatling.core.Predef._
//import scala.language.postfixOps
//import scala.concurrent.duration._
//import com.pharbers.gatling.scenario._
//import com.pharbers.gatling.base.phHttpProtocol
//import io.gatling.core.structure.ScenarioBuilder
//
//class courseLst extends Simulation {
//	import com.pharbers.gatling.base.phHttpProtocol.{noneBlackList, noneWhiteList}
//
//	val httpProtocol = phHttpProtocol("http://123.56.179.133:7434")
//
//	val scn: ScenarioBuilder = scenario("courseLst")
//		.exec(
//			courseLst.courseLst.pause(5 seconds)
//		)
//
//	setUp(scn.inject(rampUsers(100) over (3 seconds))).protocols(httpProtocol)
//}
