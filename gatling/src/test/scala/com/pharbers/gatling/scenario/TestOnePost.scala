package com.pharbers.gatling.scenario

import io.gatling.core.Predef._
import io.gatling.http.Predef.http
import com.pharbers.gatling.base.phToken
import io.gatling.http.request.RawFileBody
import io.gatling.core.structure.ChainBuilder
import com.pharbers.gatling.base.phHeaders.headers_json_token

object TestOnePost {
    def run(name: String)(implicit token: phToken): ChainBuilder = {
        val testFile = s"data/$name.json"
        println(testFile)
        exec(http(name)
                .post("/api/v1/" + name + "/0")
                .headers(headers_json_token)
                .body(RawFileBody(testFile)).asJSON
        )
    }
}
