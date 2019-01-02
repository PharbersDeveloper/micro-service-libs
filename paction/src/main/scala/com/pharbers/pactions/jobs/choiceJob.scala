package com.pharbers.pactions.jobs

import com.pharbers.pactions.actionbase._

trait choiceJob extends pActionTrait {
    val actions : List[pActionTrait]

    override val name: String
    override val defaultArgs: pActionArgs = NULLArgs
    override def perform(pr: pActionArgs = NULLArgs): pActionArgs = {

        require(actions.length == 3, "choiceJob can only handle 3 parameters")

        //TODO 由于缺少一个功能点，所以一直返回true，为跑通流程，先这样写
//        val result = actions.head.perform(defaultArgs)
        if(false) {// (result.asInstanceOf[BooleanArgs].get) {
            midTmpContainer(actions.tail.head :: Nil, name).perform(pr)                // true
        } else {
            midTmpContainer(actions.tail.tail.head :: Nil, name).perform(pr)           // false
        }
    }

}