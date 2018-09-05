package com.pharbers.testMacros.model

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, One2OneConn, ToStringMacro}

@One2OneConn[phase]("current")
@One2ManyConn[phase]("past")
@ToStringMacro
class scenario extends commonEntity {
    var uuid: String = ""
    var user_id: String = ""
    var proposal_id: String = ""
    var timestamp: Long = 0L
    var current_phase: Int = 0
    var total_phase: Int = 0
    var assess_report: String = ""
}
