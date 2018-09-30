package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class assess_report extends commonEntity  {
    var proposal_id: String = ""
    var uuid: String = ""
    var timestamp: Int = 0
    var phase: Int = 0
    var assessment: Map[String, List[Any]] = Map.empty
}