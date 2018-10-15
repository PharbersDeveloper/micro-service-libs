package com.pharbers.gatling.scenario

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.ChainBuilder
import com.pharbers.gatling.base.phHeaders.headers_json
import com.pharbers.jsonapi.json.circe.CirceJsonapiSupport
import com.pharbers.models.request.request

object userLogin {
	val feeder = csv("loginUser.csv").random
	println(feeder)

	import io.circe.syntax._
	import com.pharbers.macros._
	import com.pharbers.models.request._
	import com.pharbers.macros.convert.jsonapi.JsonapiMacro._
	val a = new CirceJsonapiSupport{}
	import a._

	val rq = new request
	rq.res = "user"
	val eq1 = eq2c("email", "zyqi@pharbers.com")
	val eq2 = eq2c("password", "zyqi@pharbers.com")
	rq.eqcond = Some(eq1 :: eq2 :: Nil)

	val login: ChainBuilder = exec(
		http("login")
			.post("/api/v1/login/0")
			.headers(headers_json)
			.body(StringBody(toJsonapi(rq).asJson.noSpaces)).asJSON
	)
}
