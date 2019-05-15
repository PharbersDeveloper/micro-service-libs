package com.pharbers.spark.util

import org.apache.spark.sql.{DataFrame, SaveMode}
import com.pharbers.spark.session.spark_conn_instance

/**
  * Created by clock on 18-2-27.
  */
case class save2Mongo(implicit val conn_instance: spark_conn_instance) extends spark_manager_trait {
    def save2Mongo(dataFrame: DataFrame,
                 mongodbHost: String,
                 mongodbPort: String,
                 databaseName: String,
                 collName: String,
                 saveMode: SaveMode = SaveMode.Append): Unit = {
        dataFrame.write.format("com.mongodb.spark.sql.DefaultSource").mode(saveMode)
                .option("uri", s"mongodb://$mongodbHost:$mongodbPort/")
                .option("database", databaseName)
                .option("collection", collName)
                .save()
    }
}