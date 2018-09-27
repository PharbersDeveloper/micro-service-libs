package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class bind_user_course() extends commonEntity {
    var user_id: String = ""
    var course_id: String = ""
}
