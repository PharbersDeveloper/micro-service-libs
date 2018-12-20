package com.pharbers.pactions.jobs

import com.pharbers.pactions.actionbase.{MapArgs, NULLArgs, pActionArgs, pActionTrait}

trait sequenceJobWithMap extends pActionTrait {
    val actions : List[pActionTrait]

    override val name: String
    override val defaultArgs: pActionArgs = NULLArgs
    override def perform(pr: pActionArgs = MapArgs(Map().empty)): pActionArgs = {
        if (actions.isEmpty) pr
        else {
            val tmp = pr match {
                    case mapArgs: MapArgs => MapArgs(mapArgs.get +
                            (actions.head.name -> actions.head.perform(pr)))
                    case _ => pr
                }

            midMapContainer(actions.tail, name).perform(tmp)
        }
    }
}

case class midMapContainer(override val actions : List[pActionTrait], override val name: String) extends sequenceJobWithMap