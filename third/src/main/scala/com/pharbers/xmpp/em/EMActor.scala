package com.pharbers.xmpp.em

import akka.actor.{Actor, ActorLogging, Props}
import play.api.libs.json.JsValue

case class  DDNInit()
case class 	DDNNotifyUsers(parameters : List[(String, JsValue)])
case class 	DDNCreateChatRoom(parameters : (String, JsValue)*)
case class 	DDNDismissChatRoom(parameters : (String, JsValue)*)
case class 	DDNCreateChatGroup(parameters : (String, JsValue)*)
case class 	DDNDismissChatGroup(parameters : (String, JsValue)*)

case class 	DDNRegisterUser(parameters : (String, JsValue)*)
case class 	DDNForceOfflineUser(user_id : String)

object EMActor {
    def props(emn : EMNotification) = Props(new EMActor(emn))
}

class EMActor(val emn : EMNotification) extends Actor with ActorLogging {
    def parameters2Map(parameters : List[(String, JsValue)]) : Map[String, JsValue] = {
        var pm : Map[String, JsValue] = Map.empty
        for ((key, value) <- parameters) pm += key -> value
        pm
    }

    def receive: PartialFunction[Any, Unit] = {
        case DDNInit() => emn.getAuthTokenForEM //DDNNotification.getAuthTokenForXMPP
        case notify : DDNNotifyUsers =>
            emn.nofity(parameters2Map(notify.parameters))
        case create : DDNCreateChatRoom =>
            sender ! emn.createChatRoom(parameters2Map(create.parameters.toList))
        case dismiss : DDNDismissChatRoom =>
            sender ! emn.dismissChatGroup(parameters2Map(dismiss.parameters.toList))
        case cg : DDNCreateChatGroup =>
            sender ! emn.createChatGroup(parameters2Map(cg.parameters.toList))
        case dg : DDNDismissChatGroup =>
            sender ! emn.dismissChatGroup(parameters2Map(dg.parameters.toList))
        case rg : DDNRegisterUser =>
            sender ! emn.registerUser(parameters2Map(rg.parameters.toList))
        case fq : DDNForceOfflineUser =>
            sender ! emn.forceOffline(fq.user_id)
        case _ =>
    }
}
