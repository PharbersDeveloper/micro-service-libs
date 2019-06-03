package com.pharbers.spark

import com.pharbers.spark.util.spark_manager_trait
import com.pharbers.spark.session.spark_conn_trait

/**
  * Created by clock on 18-2-26.
  */
case class phSparkDriver(applicationName: String) extends spark_conn_trait with spark_manager_trait with spark_run_config {
    jarsLst.foreach(addJar)

    def setUtil[T <: spark_manager_trait](helper: T): T = helper

    def addJar(jarPath: String): phSparkDriver = {
        sc.addJar(jarPath)
        this
    }

    def stopSpark(): Unit = this.ss.stop()
}