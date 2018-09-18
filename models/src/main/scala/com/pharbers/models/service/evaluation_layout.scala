package com.pharbers.models.service

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2OneConn, ToStringMacro}

@One2OneConn[capablity_radar_layout]("radar")
@One2OneConn[evaluation_card_layout]("evaluationcard")
@One2OneConn[evaluation_line_layout]("line")
@ToStringMacro
class evaluation_layout extends commonEntity {
    var major: Int = 1
    var minor: Int = 0
    var component_name: String = ""
}
