package com.pharbers.kafka.common

import com.pharbers.baseModules.PharbersInjectModule

/**
  * @ ProjectName micro-service-libs.com.pharbers.kafka.common.kafka_config_obj
  * @ author jeorch
  * @ date 19-6-11
  * @ Description: TODO
  */
object kafka_config_obj extends PharbersInjectModule {
    override val id: String = "kafka-config"
    override val configPath: String = "pharbers_config/kafka_config.xml"
    override val md = "broker":: "group" :: "topics" :: "acks" ::
        "keyDefaultSerializer" :: "valueDefaultSerializer" ::
        "keyDefaultDeserializer" :: "valueDefaultDeserializer" ::
        "securityProtocol" :: "sslAlgorithm" ::
        "sslTruststoreLocation" :: "sslTruststorePassword" ::
        "sslKeystoreLocation" :: "sslKeystorePassword" ::
        Nil

    lazy val broker: String = config.mc.find(p => p._1 == "broker").get._2.toString
    lazy val group: String = config.mc.find(p => p._1 == "group").get._2.toString
    lazy val topics: Array[String] = config.mc.find(p => p._1 == "topics").get._2.toString.split("##")
    lazy val acks: String = config.mc.find(p => p._1 == "acks").get._2.toString
    lazy val keyDefaultSerializer: String = config.mc.find(p => p._1 == "keyDefaultSerializer").get._2.toString
    lazy val valueDefaultSerializer: String = config.mc.find(p => p._1 == "valueDefaultSerializer").get._2.toString
    lazy val keyDefaultDeserializer: String = config.mc.find(p => p._1 == "keyDefaultDeserializer").get._2.toString
    lazy val valueDefaultDeserializer: String = config.mc.find(p => p._1 == "valueDefaultDeserializer").get._2.toString
    lazy val securityProtocol: String = config.mc.find(p => p._1 == "securityProtocol").get._2.toString
    lazy val sslAlgorithm: String = config.mc.find(p => p._1 == "sslAlgorithm").get._2.toString
    lazy val sslTruststoreLocation: String = config.mc.find(p => p._1 == "sslTruststoreLocation").get._2.toString
    lazy val sslTruststorePassword: String = config.mc.find(p => p._1 == "sslTruststorePassword").get._2.toString
    lazy val sslKeystoreLocation: String = config.mc.find(p => p._1 == "sslKeystoreLocation").get._2.toString
    lazy val sslKeystorePassword: String = config.mc.find(p => p._1 == "sslKeystorePassword").get._2.toString
}
