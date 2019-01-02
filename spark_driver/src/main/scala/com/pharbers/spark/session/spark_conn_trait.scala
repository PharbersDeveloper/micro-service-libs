package com.pharbers.spark.session

/**
  * Created by clock on 18-2-27.
  */
trait spark_conn_trait {
    val applicationName: String
    implicit val conn_instance: spark_conn_instance = spark_conn_obj(applicationName)
}
