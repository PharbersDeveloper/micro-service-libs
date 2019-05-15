package com.pharbers.spark.session

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{SQLContext, SparkSession}

/**
  * Created by clock on 18-2-26.
  */
trait spark_conn_instance { this: spark_conn_config =>

//    System.setProperty("HADOOP_USER_NAME","spark")
    val applicationName: String

    private val conf = new SparkConf()
            .set("spark.yarn.jars", yarnJars)
            .set("spark.yarn.archive", yarnJars)
            .set("yarn.resourcemanager.hostname", yarnResourceHostname)
            .set("yarn.resourcemanager.address", yarnResourceAddress)
            .setAppName(applicationName)
            .setMaster("yarn")
            .set("spark.scheduler.mode", "FAIR")
            .set("spark.sql.crossJoin.enabled", "true")
            .set("spark.yarn.dist.files", yarnDistFiles)
            .set("spark.executor.memory", executorMemory)
            .set("spark.driver.extraJavaOptions", "-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,adress=5005")
            .set("spark.executor.extraJavaOptions",
                """
                  | -XX:+UseG1GC -XX:+PrintFlagsFinal
                  | -XX:+PrintReferenceGC -verbose:gc
                  | -XX:+PrintGCDetails -XX:+PrintGCTimeStamps
                  | -XX:+PrintAdaptiveSizePolicy -XX:+UnlockDiagnosticVMOptions
                  | -XX:+G1SummarizeConcMark
                  | -XX:InitiatingHeapOccupancyPercent=35 -XX:ConcGCThreads=1
                """.stripMargin)
    
    val spark_session: SparkSession = SparkSession.builder().config(conf).getOrCreate()
    val spark_context: SparkContext = spark_session.sparkContext
    val spark_sql_context: SQLContext = spark_session.sqlContext
}
