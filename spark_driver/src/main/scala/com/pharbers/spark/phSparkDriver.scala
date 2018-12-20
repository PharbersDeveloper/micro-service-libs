package com.pharbers.spark

import com.pharbers.spark.util.spark_manager_trait
import com.pharbers.spark.session.{spark_conn_config, spark_conn_trait}

/**
  * Created by clock on 18-2-26.
  */
class phSparkDriver(override val applicationName: String) extends spark_conn_trait with spark_manager_trait {

    import phSparkDriver._

    def setUtil[T <: spark_manager_trait](helper: T): T = helper

    def stopCurrConn(): Unit = curr_conn_set -= applicationName
}

object phSparkDriver extends spark_conn_config {
    var curr_conn_set: Set[String] = Set.empty

    def currConnNum: Int = curr_conn_set.size

    def apply(applicationName: String): phSparkDriver = {
        if (curr_conn_set.contains(applicationName))
            return new phSparkDriver(applicationName)

        var wait_count: Int = 0
        while (currConnNum >= sparkParallelNum) {
            println("Please waiting for spark instance")
            Thread.sleep(1000)
            println(s"Wait $wait_count seconds!")
            wait_count += 1
            if (wait_count >= waitSeconds)
                throw new Exception("Error! Wait for spark instance time out!")
        }

        curr_conn_set += applicationName
        new phSparkDriver(applicationName)
    }
}
