package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, One2OneConn, ToStringMacro}

@One2OneConn[course]("course")
@One2ManyConn[examrequire]("examrequire")
@ToStringMacro
class bind_course_exam_require() extends commonEntity {
    var course_id: String = ""
    var exam_require_id: String = ""
}