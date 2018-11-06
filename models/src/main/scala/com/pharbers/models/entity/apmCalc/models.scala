package com.pharbers.models.entity.apmCalc

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class models extends commonEntity {
    var proposal_id = ""
    var timestamp = 0
    var value: List[Map[String, AnyRef]] = Nil
}
