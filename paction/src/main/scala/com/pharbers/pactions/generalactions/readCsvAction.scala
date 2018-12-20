package com.pharbers.pactions.generalactions

import com.pharbers.pactions.actionbase._
import com.pharbers.spark.phSparkDriver
import com.pharbers.spark.util.readCsv

object readCsvAction {
    def apply(arg_path: String,
              delimiter: String = ",",
              arg_name: String = "readCsvJob",
              applicationName: String = "test-dirver"): pActionTrait =
        new readCsvAction(StringArgs(arg_path), delimiter, arg_name, applicationName)
}

class readCsvAction(override val defaultArgs: pActionArgs,
                    delimiter: String,
                    override val name: String,
                    applicationName: String) extends pActionTrait {
    override def perform(args: pActionArgs = NULLArgs): pActionArgs = {
        val driver = phSparkDriver(applicationName)
        import driver.conn_instance
        DFArgs(driver.setUtil(readCsv())readCsv(defaultArgs.asInstanceOf[StringArgs].get, delimiter))
    }

}
