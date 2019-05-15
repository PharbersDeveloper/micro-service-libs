package com.pharbers.pactions.generalactions

import com.pharbers.spark.util.readCsv
import com.pharbers.spark.phSparkDriver
import com.pharbers.pactions.actionbase._

object readCsvAction {
    def apply(arg_path: String,
              delimiter: String = 31.toChar.toString,
              arg_name: String = "csv2DFAction")(implicit sparkDriver: phSparkDriver): pActionTrait =
        new readCsvAction(StringArgs(arg_path), arg_name, delimiter)
}

class readCsvAction(override val defaultArgs: pActionArgs,
                    override val name: String,
                    delimiter: String)(implicit sparkDriver: phSparkDriver) extends pActionTrait {
    override def perform(args: pActionArgs = NULLArgs): pActionArgs = {
        DFArgs(
            sparkDriver
                    .setUtil(readCsv()(sparkDriver.conn_instance))
                    .readCsv(defaultArgs.asInstanceOf[StringArgs].get, delimiter)
        )
    }
}