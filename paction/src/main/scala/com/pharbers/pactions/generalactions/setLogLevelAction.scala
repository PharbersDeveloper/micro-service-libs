package com.pharbers.pactions.generalactions

import com.pharbers.spark.phSparkDriver
import com.pharbers.pactions.actionbase.{NULLArgs, pActionArgs, pActionTrait}

object setLogLevelAction {
    def apply(level: String, job_id: String) : pActionTrait =
        new setLogLevelAction(level, job_id)
}

class setLogLevelAction(level: String, job_id: String) extends pActionTrait {
    override val name: String = "setLogLevelAction"
    override val defaultArgs : pActionArgs = NULLArgs

    override def perform(args : pActionArgs): pActionArgs = {
        phSparkDriver(job_id).sc.setLogLevel(level)
        NULLArgs
    }
}