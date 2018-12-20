package com.pharbers.pactions.jobs

import com.pharbers.pactions.actionbase.{NULLArgs, pActionArgs, pActionTrait}

trait sequenceJob extends pActionTrait {
    val actions : List[pActionTrait]

    override val name: String
    override val defaultArgs: pActionArgs = NULLArgs
    override def perform(pr: pActionArgs = NULLArgs): pActionArgs = {
        if (actions.isEmpty) pr
        else midTmpContainer(actions.tail, name).perform(actions.head.perform(pr))
    }
}

case class midTmpContainer(override val actions : List[pActionTrait], override val name: String) extends sequenceJob
