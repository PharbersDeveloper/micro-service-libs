package com.pharbers.moduleConfig

trait ConfigDefines {
    val md : List[String]
}

case class ConfigImpl(mc : List[(String, AnyRef)])
