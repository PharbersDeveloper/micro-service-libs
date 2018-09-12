package com.pharbers.pattern.frame

import play.api.mvc.Request
import com.pharbers.ErrorCode._
import com.pharbers.jsonapi.model
import com.pharbers.jsonapi.model._
import akka.actor.{Actor, ActorLogging, Props}

object Gateway {
    def prop(implicit request: Request[model.RootObject]): Props = Props(new Gateway)
}

class Gateway(implicit request: Request[model.RootObject]) extends Actor with ActorLogging {
    def receive: PartialFunction[Any, Unit] = {
        case brick: Brick =>
            try {
                brick.prepare
                brick.exec
                brick.done match {
                    case Some(next_brick) => brick.forwardTo(next_brick)
                    case None => Unit
                }
                sender ! brick.goback
            } catch {
                case ex: Exception =>
                    ex.printStackTrace()
                    sender ! RootObject(errors = Some(Seq(
                        Error(
                            id = Some("500"),
                            status = Some("error"),
                            code = Some(getErrorCodeByName(ex.getMessage).toString),
                            title = Some(ex.getMessage),
                            detail = Some(getErrorMessageByName(ex.getMessage))
                        )).asInstanceOf[Errors]))
            }

        case _ => Unit
    }
}
