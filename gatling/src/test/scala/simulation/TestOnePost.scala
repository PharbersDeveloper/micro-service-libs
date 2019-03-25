package simulation

import io.gatling.core.Predef._
import gatling.scenario.OnePostScenario
import gatling.base.{phHttpProtocol, phToken}
import io.gatling.core.structure.ScenarioBuilder

import scala.language.postfixOps

class TestOnePost extends Simulation {

    import gatling.base.phHttpProtocol.{noneBlackList, noneWhiteList}

    val httpProtocol = phHttpProtocol("http://www.pharbers.com")
//    val httpProtocol = phHttpProtocol("http://123.56.179.133:18024") // apmCalc
//    val httpProtocol = phHttpProtocol("http://127.0.0.1:9000")

    implicit val implToken: phToken = phToken("5bc58327c8f5e406a2b57394")
    val name = "tm_r_pressure_test"
    val scn: ScenarioBuilder = scenario(name).exec(
        OnePostScenario.run("ad1f24bf-cd78-4fcd-a0a3-4fdcc7b59e17"),
        OnePostScenario.run("7e1258d2-f12a-4bc2-8c98-e7d79d99b587")
    )

//    setUp(scn.inject(rampUsers(200) over(5 seconds))).protocols(httpProtocol)
    setUp(scn.inject(atOnceUsers(100))).protocols(httpProtocol)
}