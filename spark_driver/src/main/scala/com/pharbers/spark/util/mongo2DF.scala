package com.pharbers.spark.util

import org.apache.spark.sql.DataFrame
import com.pharbers.spark.session.spark_conn_instance

/**
  * Created by clock on 19-3-29.
  */
case class mongo2DF(implicit val conn_instance: spark_conn_instance) extends spark_manager_trait {
    def mongo2DF(mongodbHost: String,
                 mongodbPort: String,
                 databaseName: String,
                 collName: String,
                 readPreferenceName: String = "secondaryPreferred"): DataFrame = {
        ss.read.format("com.mongodb.spark.sql.DefaultSource")
                .option("spark.mongodb.input.uri", s"mongodb://$mongodbHost:$mongodbPort/")
                .option("spark.mongodb.input.database", databaseName)
                .option("spark.mongodb.input.collection", collName)
                .option("readPreference.name", readPreferenceName)
                .load()
    }
}