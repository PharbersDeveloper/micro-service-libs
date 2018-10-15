package com.pharbers.gatling.scenario

import com.pharbers.gatling.base.phHeaders.headers_base
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

object courseLst {
	val courseLst: ChainBuilder = exec(http("courseLst")
			.get("/api/v1/courseLst/0")
			.headers(headers_base))
}
