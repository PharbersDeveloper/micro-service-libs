package com.pharbers.pactions.generalactions

import com.pharbers.spark.phSparkDriver
import com.pharbers.pactions.actionbase._
import com.pharbers.spark.util.readParquet

object readParquetAction {
    def apply(arg_path: String,
              arg_name: String = "readParquetAction")(implicit sparkDriver: phSparkDriver): pActionTrait =
        new readParquetAction(StringArgs(arg_path), arg_name)
}

class readParquetAction(override val defaultArgs: pActionArgs,
                        override val name: String)(implicit sparkDriver: phSparkDriver) extends pActionTrait {
    override def perform(args: pActionArgs = NULLArgs): pActionArgs = {
        DFArgs(
            sparkDriver
                    .setUtil(readParquet()(sparkDriver.conn_instance))
                    .readParquet(defaultArgs.asInstanceOf[StringArgs].get)
        )
    }
}