package com.pharbers.spark.util

import org.apache.spark.SparkContext
import com.pharbers.spark.session.spark_conn_instance
import org.apache.spark.sql.{SQLContext, SparkSession}

/**
  * Created by clock on 18-2-27.
  */
trait spark_manager_trait {
    implicit val conn_instance: spark_conn_instance
    val ss: SparkSession = conn_instance.spark_session
    val sc: SparkContext = conn_instance.spark_context
    val sqc: SQLContext = conn_instance.spark_sql_context
}