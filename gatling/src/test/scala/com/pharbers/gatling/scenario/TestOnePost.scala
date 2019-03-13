package com.pharbers.gatling.scenario

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef.{RawFileBody, http}
import com.pharbers.gatling.base.phHeaders.{phToken, headers_json_token}

object TestOnePost {
    def run(name: String)(implicit token: phToken):ChainBuilder ={
//        val testFile = s"data/$name.json"
//        println(testFile)
        exec(http(name).get("/v0/representativeConfigs/5c7e3e05eeefcc1c9ec104d1/representative"))
//                .post("/api/v1/" + name +"/0")
//                .headers(headers_json_token)
//                .body(RawFileBody(testFile)).asJSON)
    }
}
