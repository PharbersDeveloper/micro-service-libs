package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, One2OneConn, ToStringMacro}

@One2OneConn[course]("course")
@One2ManyConn[action_plan]("action_plan")
@ToStringMacro
class bind_course_action_plan() extends commonEntity {
    var course_id: String = ""
    var plan_id: String = ""
}