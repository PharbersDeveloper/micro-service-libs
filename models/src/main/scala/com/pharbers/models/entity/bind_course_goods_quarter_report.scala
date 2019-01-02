package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, One2OneConn, ToStringMacro}

@ToStringMacro
class bind_course_goods_quarter_report() extends commonEntity {
    var course_id: String = ""
    var goods_id: String = ""
    var quarter_report_id: String = ""
}