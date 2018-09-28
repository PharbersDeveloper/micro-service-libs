package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class bind_course_region_business() extends commonEntity {
    var course_id: String = ""
    var region_id: String = ""
    var business_id: String = ""
}