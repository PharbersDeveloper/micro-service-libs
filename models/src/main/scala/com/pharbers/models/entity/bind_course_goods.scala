package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class bind_course_goods() extends commonEntity {
    var course_id: String = ""
    var goods_id: String = ""
}