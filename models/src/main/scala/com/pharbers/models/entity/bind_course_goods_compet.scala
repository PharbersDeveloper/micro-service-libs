package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, One2OneConn, ToStringMacro}

@One2OneConn[course]("course")
@One2OneConn[medicine]("medicine")
@One2ManyConn[medicine]("compet")
@ToStringMacro
class bind_course_goods_compet() extends commonEntity {
    var course_id: String = ""
    var goods_id: String = ""
    var compet_id: String = ""
}
