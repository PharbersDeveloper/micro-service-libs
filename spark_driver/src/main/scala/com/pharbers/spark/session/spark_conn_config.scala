package com.pharbers.spark.session

import com.pharbers.baseModules.PharbersInjectModule

/**
  * Created by clock on 18-2-26.
  */
trait spark_conn_config extends PharbersInjectModule {

    override val id: String = "spark-config"
    override val configPath: String = "pharbers_config/spark-config.xml"
    override val md = "parallel-number" :: "wait-seconds" :: Nil

    protected val sparkParallelNum: Int = config.mc.find(p => p._1 == "parallel-number").get._2.toString.toInt
    protected val waitSeconds: Int = config.mc.find(p => p._1 == "wait-seconds").get._2.toString.toInt
}
