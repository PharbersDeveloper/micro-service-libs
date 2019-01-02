package com.pharbers.spark.util

import com.pharbers.spark.session.spark_conn_instance
import org.apache.spark.sql.DataFrame

/**
  * Created by clock on 18-2-27.
  */
case class readCsv(implicit val conn_instance: spark_conn_instance) extends spark_manager_trait {
    def readCsv(file_path: String, delimiter: String = ","): DataFrame = {
        ss.read.format("com.databricks.spark.csv")
                .option("header", "true")
                .option("delimiter", delimiter)
                .load(file_path)
    }
}