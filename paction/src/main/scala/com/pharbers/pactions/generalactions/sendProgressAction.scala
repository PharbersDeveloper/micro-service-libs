package com.pharbers.pactions.generalactions

import com.pharbers.pactions.actionbase.{MapArgs, NULLArgs, pActionArgs, pActionTrait}

object sendProgressAction {
    def apply(content: MapArgs, arg_name: String = "sendProgressAction")
             (implicit send: MapArgs => Unit): pActionTrait =
        new sendProgressAction(content, arg_name)
}

class sendProgressAction(override val defaultArgs: MapArgs,
                         override val name: String)(implicit send: MapArgs => Unit) extends pActionTrait {
    override def perform(args: pActionArgs = NULLArgs): pActionArgs = {
        send(defaultArgs)
        NULLArgs
    }
}