package com.pharbers.xmpp.em

import akka.actor.ActorSystem
import com.pharbers.xmpp.DDNTrait
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await
import scala.language.postfixOps

trait EMImpl extends DDNTrait {

    implicit val time_out: Timeout = Timeout(3 second)
    val emn : EMNotification = EMNotification(
        app_key = config.mc.find(p => p._1 == "app_key").get._2.toString,
        org_name = config.mc.find(p => p._1 == "org_name").get._2.toString,
        app_name = config.mc.find(p => p._1 == "app_name").get._2.toString,
        client_id = config.mc.find(p => p._1 == "client_id").get._2.toString,
        client_secret = config.mc.find(p => p._1 == "client_secret").get._2.toString,
        notification_account = config.mc.find(p => p._1 == "notification_account").get._2.toString,
        notification_password = config.mc.find(p => p._1 == "notification_password").get._2.toString,
        em_host = config.mc.find(p => p._1 == "em_host").get._2.toString
    )

    initDDN
    def initDDN: Unit = emn.getAuthTokenForEM

    def notifyAsync(parameters : (String, JsValue)*)(implicit as : ActorSystem): Unit = {
        val a = as.actorOf(EMActor.props(emn))
        a ! DDNNotifyUsers(parameters.toList)
    }

    def registerForDDN(user_id : String)(implicit as : ActorSystem) : JsValue = {
//        val a = as.actorOf(Props[EMActor])
        val a = as.actorOf(EMActor.props(emn))
        val f = a ? DDNRegisterUser("username" -> toJson(user_id),
                                    "password" -> toJson(emn.notification_password),
                                    "nickname" -> toJson(user_id))

        Await.result(f.mapTo[JsValue], time_out.duration)
    }

    def forceOffline(user_id : String)(implicit as : ActorSystem) : JsValue = {
        val a = as.actorOf(EMActor.props(emn))
        val f = a ? DDNForceOfflineUser(user_id)

        Await.result(f.mapTo[JsValue], time_out.duration)
    }
}
