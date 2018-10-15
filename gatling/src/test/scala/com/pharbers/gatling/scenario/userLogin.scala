package com.pharbers.gatling.scenario

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.ChainBuilder

import com.pharbers.gatling.base.phHeaders.headers_json

object userLogin {
	val feeder = csv("loginUser.csv").random
	println(feeder)

	val login: ChainBuilder = exec(http("login")
			.get("/api/user/login")
			.headers(headers_json)
			.body(StringBody("""{ "condition" :  { "email" : "nhwa", "password" : "nhwa" } }""")).asJSON)
}
