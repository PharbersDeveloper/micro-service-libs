package com.pharbers.gatling.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class qGatlingTest extends Simulation {

	val httpProtocol = http
			.baseURL("http://123.56.179.133:18002")
			.inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.(t|o)tf""", """.*\.png"""), WhiteList())
			.acceptHeader("application/json, text/javascript, */*; q=0.01")
			.acceptEncodingHeader("gzip, deflate")
			.acceptLanguageHeader("zh-CN,zh;q=0.9,zh-TW;q=0.8")
			.doNotTrackHeader("1")
			.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Safari/537.36")

	val headers_0 = Map(
		"Content-Type" -> "application/x-www-form-urlencoded; charset=UTF-8",
		"Origin" -> "http://192.168.100.254",
		"X-Requested-With" -> "XMLHttpRequest")

	val headers_1 = Map(
		"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8",
		"Upgrade-Insecure-Requests" -> "1")

	val headers_2 = Map(
		"Content-Type" -> "application/json,charset=utf-8",
		"Origin" -> "http://192.168.100.176:9000",
		"X-Requested-With" -> "XMLHttpRequest")

	val headers_3 = Map(
		"Accept" -> "*/*",
		"Content-Type" -> "text/plain;charset=UTF-8",
		"Origin" -> "http://192.168.100.176:9000")

    val uri1 = "http://a1.easemob.com/1111170725178064/maxloading/token"
    val uri3 = "http://192.168.100.254/cgi-bin/luci/;stok=e3ea99128c07142034023a0ab3de3b9d/apmngr_status"

	val scn = scenario("qi_gatling_test")
		.exec(http("cgi-bin")
			.post(uri3 + "?form=status")
			.headers(headers_0)
			.formParam("data", """{"method":"get"}"""))
		.pause(3)
		.exec(http("/")
			.get("/")
			.headers(headers_1))
		.pause(3)
		.exec(http("login")
			.post("/api/user/login")
			.headers(headers_2)
			.body(RawFileBody("RecordedSimulation_0002_request.txt"))
			.resources(http("EM")
			.post(uri1 + "")
			.headers(headers_3)
			.body(RawFileBody("RecordedSimulation_0003_request.txt")),
            http("EM_login")
			.get("/data-center")
			.headers(headers_1),
            http("search_market_all")
			.post("/api/search/market/all")
			.headers(headers_2)
			.body(RawFileBody("RecordedSimulation_0005_request.txt")),
            http("user_detail")
			.post("/api/user/detail")
			.headers(headers_2)
			.body(RawFileBody("RecordedSimulation_0006_request.txt")),
            http("cgi-bin2")
			.post(uri3 + "?form=status")
			.headers(headers_0)
			.formParam("data", """{"method":"get"}""")))

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
