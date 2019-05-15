package com.pharbers.pactions.generalactions

import com.pharbers.spark.phSparkDriver
import com.pharbers.pactions.actionbase.{NULLArgs, StringArgs, pActionArgs, pActionTrait}

object setLogLevelAction {
    def apply(level: String, arg_name: String = "setLogLevelAction")(implicit sparkDriver: phSparkDriver): pActionTrait =
        new setLogLevelAction(StringArgs(level), arg_name)
}

class setLogLevelAction(override val defaultArgs: pActionArgs,
                        override val name: String = "setLogLevelAction")(implicit sparkDriver: phSparkDriver) extends pActionTrait {
    override def perform(args : pActionArgs = NULLArgs): pActionArgs = {
        sparkDriver.sc.setLogLevel(defaultArgs.asInstanceOf[StringArgs].get)
        NULLArgs
    }
}