package com.pharbers.pattern.frame

import com.pharbers.http.{HTTP, httpOpt}

object forward {
    def apply(host: String, port: String = "9000")(api: String): httpOpt =
        HTTP(s"http://$host:$port$api")
                .header("Accept" -> "application/json", "Content-Type" -> "application/json")
}
