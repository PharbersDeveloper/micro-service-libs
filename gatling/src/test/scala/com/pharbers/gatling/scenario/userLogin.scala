package com.pharbers.gatling.scenario

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.ChainBuilder

import com.pharbers.gatling.base.phHeaders.headers_json

object userLogin {
	val feeder = csv("loginUser.csv").random
	println(feeder)

	val login: ChainBuilder = exec(http("login")
			.post("/api/v1/login/0")
			.headers(headers_json)
			.body(RawFileBody("data/login.txt")).asJSON)
}
