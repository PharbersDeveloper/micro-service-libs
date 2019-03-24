package com.pharbers.gatling.simulation

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import com.pharbers.gatling.base.{phHttpProtocol, phToken}
import com.pharbers.gatling.scenario.OAuthTest
import io.gatling.core.structure.ScenarioBuilder

import scala.language.postfixOps

class OAuthTest extends Simulation {

    import com.pharbers.gatling.base.phHttpProtocol.{noneBlackList, noneWhiteList}

    val httpProtocol = phHttpProtocol("http://www.pharbers.com:90")

    val scn: ScenarioBuilder = scenario("OAuth2.0").exec(
        OAuthTest.getLoginPage.pause(5)
        , OAuthTest.accountValidation("a", "b")
        , OAuthTest.getAuthPage
        , OAuthTest.authToken
        , OAuthTest.passwordToken("a", "b")
        , OAuthTest.tokenValidation("4OX8Q92UO3QBBCFWF-DY9G")
    )

//    setUp(scn.inject(rampUsers(200) over(5 seconds))).protocols(httpProtocol)
    setUp(scn.inject(atOnceUsers(2))).protocols(httpProtocol)
}