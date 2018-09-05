package com.pharbers.xmpp

import akka.actor.ActorSystem
import play.api.libs.json.JsValue
import com.pharbers.baseModules.PharbersInjectModule

trait DDNTrait extends PharbersInjectModule {
    def initDDN()
    def notifyAsync(parameters : (String, JsValue)*)(implicit as : ActorSystem)
    def registerForDDN(user_id : String)(implicit as : ActorSystem) : JsValue
    def forceOffline(user_id : String)(implicit as : ActorSystem) : JsValue

    override val id: String = "ddn-trait"
    override val configPath: String = "pharbers_config/ddn_trait.xml"
    override val md: List[String] = "app_key" :: "org_name" :: "app_name" :: "client_id" ::
                                    "client_secret" :: "notification_account" :: "notification_password" ::
                                    "em_host" :: Nil
}
