package com.pharbers.spark.util

import org.bson.Document
import com.mongodb.spark.MongoSpark
import com.mongodb.spark.rdd.MongoRDD
import com.mongodb.spark.config.ReadConfig
import com.pharbers.spark.session.spark_conn_instance

/**
  * Created by clock on 18-2-27.
  */
case class mongo2RDD(implicit val conn_instance: spark_conn_instance) extends spark_manager_trait {
    def mongo2RDD(mongodbHost: String,
                  mongodbPort: String,
                  databaseName: String,
                  collName: String,
                  readPreferenceName: String = "secondaryPreferred"): MongoRDD[Document] = {
        val readConfig = ReadConfig(Map(
            "spark.mongodb.input.uri" -> s"mongodb://$mongodbHost:$mongodbPort/",
            "spark.mongodb.input.database" -> databaseName,
            "spark.mongodb.input.collection" -> collName,
            "readPreference.name" -> readPreferenceName)
        )
        MongoSpark.load(sc, readConfig = readConfig)
    }
}