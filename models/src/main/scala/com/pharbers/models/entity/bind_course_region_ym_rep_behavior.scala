package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class bind_course_region_ym_rep_behavior() extends commonEntity {
    var course_id: String = ""
    var region_id: String = ""
    var ym: String = ""
    var rep_behavior_id: String = ""
}