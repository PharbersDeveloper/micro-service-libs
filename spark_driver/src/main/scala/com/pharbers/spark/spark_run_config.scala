package com.pharbers.spark

import org.apache.hadoop.fs.{FileSystem, Path}
import com.pharbers.spark.session.spark_conn_trait
import com.pharbers.baseModules.PharbersInjectModule

/**
  * Created by clock on 18-2-26.
  */
trait spark_run_config extends PharbersInjectModule { this: spark_conn_trait =>

    override val id: String = "spark-run-config"
    override val configPath: String = "pharbers_config/spark-config.xml"
    override val md: List[String] = "jars-path" :: "parallel-number" :: "wait-seconds" :: Nil

    protected val jarsPath: String = config.mc.find(p => p._1 == "jars-path").get._2.toString
    protected val sparkParallelNum: Int = config.mc.find(p => p._1 == "parallel-number").get._2.toString.toInt
    protected val waitSeconds: Int = config.mc.find(p => p._1 == "wait-seconds").get._2.toString.toInt

    protected val jarsLst: Array[String] =
        if(jarsPath.startsWith("hdfs:///"))
            FileSystem.get(conn_instance.spark_context.hadoopConfiguration)
                    .listStatus(new Path(jarsPath))
                    .map(_.getPath.toString)
        else Array.empty[String]
}