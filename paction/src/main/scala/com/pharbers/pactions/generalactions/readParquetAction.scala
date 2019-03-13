package com.pharbers.pactions.generalactions

import com.pharbers.pactions.actionbase._
import com.pharbers.spark.phSparkDriver
import com.pharbers.spark.util.readParquet

object readParquetAction {
        def apply(arg_path: String,
                  arg_name: String = "readParquetJob",
                  applicationName: String): pActionTrait =
            new readParquetAction(StringArgs(arg_path), arg_name, applicationName)
}

class readParquetAction(override val defaultArgs: pActionArgs,
                    override val name: String,
                    applicationName: String) extends pActionTrait {
        override def perform(args: pActionArgs = NULLArgs): pActionArgs = {
            val driver = phSparkDriver(applicationName)
            import driver.conn_instance
            DFArgs(driver.setUtil(readParquet()).readParquet(defaultArgs.asInstanceOf[StringArgs].get))
        }
}