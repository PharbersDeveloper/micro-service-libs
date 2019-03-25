package gatling

import gatling.base.phHeaders._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.core.feeder.SourceFeederBuilder
import io.gatling.core.Predef.{RawFileBody, csv, exec}

/**
  * @description:
  * @author: clock
  * @date: 2019-03-25 12:15
  */
package object scenario {
    val getHome: ChainBuilder = exec(http("home")
            .get("/")
            .headers(headers_base))

    lazy val feeder: SourceFeederBuilder[String] = csv("loginUser.csv").random
    val login: ChainBuilder = exec(http("login")
            .post("/api/v1/login/0")
            .headers(headers_json)
            .body(RawFileBody("data/login.txt")).asJson)
}
