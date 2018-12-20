package com.pharbers.pactions.jobs 
 
import com.pharbers.pactions.actionbase._ 
 
trait choice3pJob extends pActionTrait { 
    val actions : List[pActionTrait] 
 
    override val name: String 
    override val defaultArgs: pActionArgs = NULLArgs 
    override def perform(pr: pActionArgs = NULLArgs): pActionArgs = { 
 
        require(actions.length == 3, "choiceJob can only handle 3 parameters") 
 
        val result = actions.head.perform(defaultArgs) 
        if(result.asInstanceOf[BooleanArgs].get) { 
            midTmpContainer(actions.tail.head :: Nil, name).perform(pr)                // true 
        } else { 
            midTmpContainer(actions.tail.tail.head :: Nil, name).perform(pr)           // false 
        } 
    } 
 
}