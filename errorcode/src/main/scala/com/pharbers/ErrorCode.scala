package com.pharbers

import play.api.libs.json.Json
import play.api.libs.json.Json._
import play.api.libs.json.JsValue
import com.pharbers.baseModules.PharbersSingletonModule
import com.pharbers.moduleConfig.{ConfigDefines, ConfigImpl}

import scala.xml.Node

object ErrorCode extends PharbersSingletonModule {
    override val id: String = "error_code"
    override val configPath: String = "pharbers_config/error_code.xml"
    override val md: List[String] = "error" :: Nil

    import com.pharbers.moduleConfig.ModuleConfig.fr
    implicit val f : (ConfigDefines, Node) => ConfigImpl = { (c, n) =>
        ConfigImpl(
            c.md map { x => x -> ((n \ x).toList map { iter =>
                new ErrorNode((iter \\ "@reason").toString,
                              (iter \\ "@code").toString.toInt,
                              (iter \\ "@description").toString)
            })}
        )}
    override lazy val config: ConfigImpl = loadConfig(configDir + "/" + configPath)

    case class ErrorNode(name: String, code: Int, message: String)

    lazy val xls: List[ErrorNode] = config.mc.find(p => p._1 == "error").get._2.asInstanceOf[List[ErrorNode]]

    def getErrorNameByCode(code: Int): String = (xls.find(x => x.code == code)) match {
        case Some(y) => y.name
        case None => "unknown error"
    }

    def getErrorCodeByName(name: String): Int = (xls.find(x => x.name == name)) match {
        case Some(y) => y.code
        case None => -9999
    }

    def getErrorMessageByName(name: String): String = (xls.find(x => x.name == name)) match {
        case Some(y) => y.message
        case None => "unknow error"
    }

    def errorToJson(name: String): JsValue =
        Json.toJson(Map("status" -> toJson("error"), "error" ->
            toJson(Map("code" -> toJson(this.getErrorCodeByName(name)), "message" -> toJson(this.getErrorMessageByName(name))))))

    def successToJson(result: JsValue = toJson("OK"), page: JsValue = toJson("")): Option[Map[String,JsValue]] = {
        Some(Map("status" -> toJson("success"), "result" ->
            toJson(Map("result" -> result, "page" -> page))))
    }
}