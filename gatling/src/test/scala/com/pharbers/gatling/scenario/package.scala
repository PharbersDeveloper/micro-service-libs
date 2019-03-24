package com.pharbers.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.core.feeder.RecordSeqFeederBuilder
import com.pharbers.gatling.base.phHeaders.{headers_base, headers_json}

/**
  * @description:
  * @author: clock
  * @date: 2019-03-24 21:25
  */
package object scenario {
    val getHome: ChainBuilder = exec(http("home")
            .get("/")
            .headers(headers_base))

    lazy val feeder: RecordSeqFeederBuilder[String] = csv("loginUser.csv").random
    val login: ChainBuilder = exec(http("login")
            .post("/api/v1/login/0")
            .headers(headers_json)
            .body(RawFileBody("data/login.txt")).asJSON)
}
