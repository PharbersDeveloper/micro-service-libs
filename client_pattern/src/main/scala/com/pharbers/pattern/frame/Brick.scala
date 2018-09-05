package com.pharbers.pattern.frame

import com.pharbers.jsonapi.model
import play.api.mvc.Request

trait Brick {

    val brick_name: String
    implicit val request: Request[model.RootObject]

    lazy val url = request.uri.split("/").filter(_.nonEmpty)
    lazy val (api, cur_step) = (url.init.mkString("/", "/", "/"), url.last.toInt)

    def prepare: Unit

    def exec: Unit

    def done: Option[String]

    def forwardTo(next_brick: String)

    def goback: model.RootObject
}
