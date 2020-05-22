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
    override val configPath: String = scala.util.Properties.envOrElse("PHA_CONF_HOME", "pharbers_config") + "/kafka_config.xml"
    override val md = "broker":: "group" :: "topics" :: "acks" ::
        "keyDefaultSerializer" :: "valueDefaultSerializer" ::
        "keyDefaultDeserializer" :: "valueDefaultDeserializer" ::
        "schemaRegistryUrl" :: "specificAvroReader" ::
//        "securityProtocol" :: "sslAlgorithm" ::
//        "sslTruststoreLocation" :: "sslTruststorePassword" ::
//        "sslKeystoreLocation" :: "sslKeystorePassword" ::
        Nil

    lazy val broker: String = config.mc.find(p => p._1 == "broker").getOrElse(("", ""))._2.toString
    lazy val group: String = config.mc.find(p => p._1 == "group").getOrElse(("", ""))._2.toString
    lazy val topics: Array[String] = config.mc.find(p => p._1 == "topics").getOrElse(("", ""))._2.toString.split("##")
    lazy val acks: String = config.mc.find(p => p._1 == "acks").getOrElse(("", ""))._2.toString
    lazy val keyDefaultSerializer: String = config.mc.find(p => p._1 == "keyDefaultSerializer").getOrElse(("", ""))._2.toString
    lazy val valueDefaultSerializer: String = config.mc.find(p => p._1 == "valueDefaultSerializer").getOrElse(("", ""))._2.toString
    lazy val keyDefaultDeserializer: String = config.mc.find(p => p._1 == "keyDefaultDeserializer").getOrElse(("", ""))._2.toString
    lazy val valueDefaultDeserializer: String = config.mc.find(p => p._1 == "valueDefaultDeserializer").getOrElse(("", ""))._2.toString
    lazy val schemaRegistryUrl: String = config.mc.find(p => p._1 == "schemaRegistryUrl").getOrElse(("", ""))._2.toString
    lazy val specificAvroReader: String = config.mc.find(p => p._1 == "specificAvroReader").getOrElse(("", ""))._2.toString
    lazy val securityProtocol: String = config.mc.find(p => p._1 == "securityProtocol").getOrElse(("", ""))._2.toString
    lazy val sslAlgorithm: String = config.mc.find(p => p._1 == "sslAlgorithm").getOrElse(("", ""))._2.toString
    lazy val sslTruststoreLocation: String = config.mc.find(p => p._1 == "sslTruststoreLocation").getOrElse(("", ""))._2.toString
    lazy val sslTruststorePassword: String = config.mc.find(p => p._1 == "sslTruststorePassword").getOrElse(("", ""))._2.toString
    lazy val sslKeystoreLocation: String = config.mc.find(p => p._1 == "sslKeystoreLocation").getOrElse(("", ""))._2.toString
    lazy val sslKeystorePassword: String = config.mc.find(p => p._1 == "sslKeystorePassword").getOrElse(("", ""))._2.toString
}
