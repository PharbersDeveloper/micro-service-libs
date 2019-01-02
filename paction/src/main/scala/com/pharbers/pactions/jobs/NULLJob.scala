package com.pharbers.pactions.jobs

import com.pharbers.pactions.actionbase.{NULLArgs, pActionArgs, pActionTrait}

object NULLJob extends pActionTrait {
    override val name: String = ""
    override val defaultArgs: pActionArgs = NULLArgs
    override def perform(pr: pActionArgs): pActionArgs = NULLArgs
}