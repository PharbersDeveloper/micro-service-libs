package com.pharbers.spark.util

import com.pharbers.spark.session.spark_conn_instance
import org.apache.spark.sql.DataFrame

case class readParquet(implicit val conn_instance: spark_conn_instance) extends spark_manager_trait {
    def readParquet(file_path: String): DataFrame = {
        ss.read.option("header", "true")
            .parquet(file_path)
    }
}