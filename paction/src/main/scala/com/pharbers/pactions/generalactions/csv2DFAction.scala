package com.pharbers.pactions.generalactions

import com.pharbers.pactions.actionbase.{DFArgs, _}
import com.pharbers.spark.phSparkDriver
import com.pharbers.spark.util.csv2RDD

object csv2DFAction {
    def apply(arg_path: String,
              delimiter: String = 31.toChar.toString,
              arg_name: String = "csv2RddJob",
              applicationName: String = "test-dirver"): pActionTrait =
        new csv2DFAction(StringArgs(arg_path), delimiter, arg_name, applicationName)
}

class csv2DFAction(override val defaultArgs: pActionArgs,
                   delimiter: String,
                   override val name: String,
                   applicationName: String) extends pActionTrait {

    override def perform(args: pActionArgs = NULLArgs): pActionArgs = {
        val driver = phSparkDriver(applicationName)
        import driver.conn_instance
        DFArgs(driver.setUtil(csv2RDD()).csv2RDD(defaultArgs.asInstanceOf[StringArgs].get, delimiter))
    }

}