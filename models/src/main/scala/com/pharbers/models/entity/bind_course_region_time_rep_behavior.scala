package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2OneConn, ToStringMacro}

@One2OneConn[repbehaviorreport]("repbehaviorreport")
@ToStringMacro
class bind_course_region_time_rep_behavior() extends commonEntity {
    var course_id: String = ""
    var region_id: String = ""
    var time_type: String = ""
    var time: String = ""
    var rep_behavior_id: String = ""
}