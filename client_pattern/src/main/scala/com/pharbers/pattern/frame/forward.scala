package com.pharbers.pattern.frame

import play.api.mvc.Request
import com.pharbers.http.{HTTP, httpOpt}

object forward {
    def apply(host: String, port: String = "9000")(api: String)(implicit rq: Request[_]): httpOpt =
        HTTP(s"http://$host:$port$api").header(rq.headers.headers: _*)
}
