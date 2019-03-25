package gatling.scenario

import gatling.base.phToken
import gatling.base.phHeaders._
import io.gatling.http.Predef._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder

/**
  * @description:
  * @author: clock
  * @date: 2019-03-25 12:15
  */
object OnePostScenario {
    def run(name: String)(implicit token: phToken): ChainBuilder = {
        val testFile = s"data/$name.json"
        println(testFile)
        exec(http(name)
                .post("/api/v1/" + name + "/0")
                .headers(headers_json_token)
                .body(RawFileBody(testFile)).asJson
        )
    }
}
