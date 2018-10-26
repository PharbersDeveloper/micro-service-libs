package com.pharbers.gatling.scenario

import com.pharbers.gatling.base.phHeaders.headers_json_token
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.ChainBuilder

object TestOnePost {
    def run(string: String)(implicit token: String):ChainBuilder ={
        println("data/" + string +".txt")
        exec(http(string)
                .post("/api/v1/" + string +"/0")
                .headers(headers_json_token)
                .body(RawFileBody("data/" + string +".txt")).asJSON)
    }
//    val exam: ChainBuilder = exec(http("exam")
//            .post("/api/v1/exam/0")
//            .headers(headers_json_token)
//            .body(RawFileBody("data/exam.txt")).asJSON)
}
