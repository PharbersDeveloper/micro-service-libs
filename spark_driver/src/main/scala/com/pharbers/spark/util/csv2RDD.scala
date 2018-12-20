package com.pharbers.spark.util

import com.pharbers.spark.session.spark_conn_instance
import org.apache.spark.sql.DataFrame

/**
  * Created by clock on 18-2-27.
  */
case class csv2RDD(implicit val conn_instance: spark_conn_instance) extends spark_manager_trait {
    def csv2RDD(file_path: String,
                delimiter: String = ",", header: Boolean = true): DataFrame = {
        ss.read.format("csv")
                .option("header", header)
                .option("inferSchema", true.toString)
                .option("mode", "DROPMALFORMED")
                .option("delimiter", delimiter)
                .csv(file_path)
    }
}