package com.pharbers.spark.session

import com.pharbers.baseModules.PharbersInjectModule

/**
  * Created by clock on 18-2-26.
  */
trait spark_conn_config extends PharbersInjectModule {

    override val id: String = "spark-config"
    override val configPath: String = "pharbers_config/spark-config.xml"
    override val md: List[String] = "yarn-jars" :: "yarn-resource-address" ::
            "yarn-resource-hostname" :: "yarn-dist-files" :: "executor-memory" :: Nil

    protected val yarnJars: String = config.mc.find(p => p._1 == "yarn-jars").get._2.toString
    protected val yarnResourceHostname: String = config.mc.find(p => p._1 == "yarn-resource-hostname").get._2.toString
    protected val yarnResourceAddress: String = config.mc.find(p => p._1 == "yarn-resource-address").get._2.toString
    protected val yarnDistFiles: String = config.mc.find(p => p._1 == "yarn-dist-files").get._2.toString
    protected val executorMemory: String = config.mc.find(p => p._1 == "executor-memory").get._2.toString
}