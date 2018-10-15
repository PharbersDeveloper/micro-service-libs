package com.pharbers.gatling.base

object phHeaders {

    val headers_base = Map(
        "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8",
        "Upgrade-Insecure-Requests" -> "1")

    val headers_json = Map(
        "Content-Type" -> "application/json,charset=utf-8"
    )

    val headers_json_token = Map(
        "Content-Type" -> "application/json,charset=utf-8",
        "Authorization" -> "bearer 5bc48ce8c0e37675f8e05ff3"
    )
}
