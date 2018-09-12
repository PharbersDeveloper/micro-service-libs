package com.pharbers.models.service

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting._

@One2OneConn[dropdown_layout]("dropdown")
@One2OneConn[report_card_layout]("reportcard")
@One2OneConn[report_table_layout]("reporttable")
@ToStringMacro
class report_layout() extends commonEntity {
    var major: Int = 1
    var minor: Int = 0
    var component_name: String = ""
}