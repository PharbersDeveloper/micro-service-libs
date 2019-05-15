package com.pharbers.spark.util

import com.pharbers.spark.session.spark_conn_instance
import org.apache.spark.sql.DataFrame

case class readParquet(implicit val conn_instance: spark_conn_instance) extends spark_manager_trait {
    def readParquet(file_path: String, header: Boolean = true): DataFrame = {
        ss.read.option("header", header.toString).parquet(file_path)
    }
}