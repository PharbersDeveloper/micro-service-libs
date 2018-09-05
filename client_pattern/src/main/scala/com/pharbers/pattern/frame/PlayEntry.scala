package com.pharbers.pattern.frame

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import com.pharbers.jsonapi.model
import com.pharbers.jsonapi.model.RootObject
import javax.inject.Inject
import play.api.libs.Files.TemporaryFile
import play.api.libs.json.JsValue
import play.api.mvc._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

object PlayEntry {
    def apply()(implicit akkasys: ActorSystem, cc: ControllerComponents) = new PlayEntry()
}

class PlayEntry @Inject()(implicit akkasys: ActorSystem, cc: ControllerComponents) extends AbstractController(cc) {
    implicit val t: Timeout = Timeout(5 second)

    def excution(brick: Brick)(implicit request: Request[model.RootObject]): RootObject = {
        val act = akkasys.actorOf(Gateway.prop)
        val r = act ? brick
        Await.result(r.mapTo[RootObject], t.duration)
    }

    def uploadRequestArgs(request: Request[AnyContent])(func: MultipartFormData[TemporaryFile] => JsValue): Result = {
        try {
            request.body.asMultipartFormData.map { x =>
                Ok(func(x))
            }.getOrElse(BadRequest("Bad Request for input"))
        } catch {
            case _: Exception => BadRequest("Bad Request for input")
        }
    }
}
