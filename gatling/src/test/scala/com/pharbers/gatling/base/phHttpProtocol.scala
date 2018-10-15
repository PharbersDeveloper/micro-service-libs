package com.pharbers.gatling.base

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.config.HttpProtocolBuilder

object phHttpProtocol {
    implicit val noneWhiteList: io.gatling.core.filter.WhiteList = WhiteList()
    implicit val noneBlackList: io.gatling.core.filter.BlackList = BlackList()
    implicit val staticBlackList: io.gatling.core.filter.BlackList = BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.(t|o)tf""", """.*\.png""")
    implicit val staticWhiteList: io.gatling.core.filter.WhiteList = WhiteList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.(t|o)tf""", """.*\.png""")

    def apply(host: String)
             (implicit blackLst: io.gatling.core.filter.BlackList, whiteLst: io.gatling.core.filter.WhiteList): HttpProtocolBuilder = { http
                .baseURL(host)
                .inferHtmlResources(blackLst, whiteLst)
                .acceptHeader("application/json, text/javascript, */*; q=0.01")
                .acceptEncodingHeader("gzip, deflate")
                .acceptLanguageHeader("zh-CN,zh;q=0.9,zh-TW;q=0.8")
                .doNotTrackHeader("1")
                .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Safari/537.36")
    }
}
