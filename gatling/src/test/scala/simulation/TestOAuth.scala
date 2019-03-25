package simulation

import io.gatling.core.Predef._
import scala.language.postfixOps
import gatling.base.phHttpProtocol
import gatling.scenario.OAuthScenario._
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.ScenarioBuilder

class TestOAuth extends Simulation {

    import gatling.base.phHttpProtocol.{noneBlackList, noneWhiteList}

    val httpProtocol = phHttpProtocol("http://123.56.179.133:90")

    val scn: ScenarioBuilder = scenario("OAuth2.0")
            .exec(getLoginPage).pause(5)
            .exec(getAuthPage).pause(5)
            .exec(accountValidation("a", "b"))
            .exec(passwordToken("a", "b"))
            .exec(tokenValidation("QOVHVLACNAYASVQXBTVEQG"))

//    setUp(scn.inject(rampUsers(200) over(5 seconds))).protocols(httpProtocol)
    setUp(scn.inject(atOnceUsers(300))).protocols(httpProtocol)
}