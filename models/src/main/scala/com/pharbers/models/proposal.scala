package com.pharbers.models

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, ToStringMacro}

@One2ManyConn[phase]("default_phases")
@ToStringMacro
class proposal extends commonEntity {
    var proposal_name: String = ""
    var proposal_des: String = ""
}
