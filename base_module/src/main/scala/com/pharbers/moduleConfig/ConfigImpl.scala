package com.pharbers.moduleConfig

trait ConfigDefines {
    val md : List[String]
}

case class ConfigImpl(val mc : List[(String, AnyRef)])
