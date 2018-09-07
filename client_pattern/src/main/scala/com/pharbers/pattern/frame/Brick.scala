package com.pharbers.pattern.frame

import com.pharbers.jsonapi.model
import com.pharbers.pattern.BrickRegistry
import play.api.mvc.Request

trait Brick {

    val brick_name: String
    implicit val rq: Request[model.RootObject]

    lazy val url = rq.uri.split("/").filter(_.nonEmpty)
    lazy val (api, cur_step) = (url.init.mkString("/", "/", "/"), url.last.toInt)

    def prepare: Unit

    def exec: Unit

    def done: Option[String] = {
        val bricks = BrickRegistry().registryRoute(api)
        if (bricks.size - 1 <= cur_step)
            None
        else
            Some(bricks(cur_step + 1))
    }

    def forwardTo(next_brick: String): Unit = {}

    def goback: model.RootObject
}
