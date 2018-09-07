package com.pharbers.testMacros.model

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class proposal extends commonEntity {
    type phase = Map[String, Any]
    var proposal_name: String = ""
    var proposal_des: String = ""
    val default_phases: List[phase] = Nil
}
