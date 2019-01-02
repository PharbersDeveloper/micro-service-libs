package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2OneConn, ToStringMacro}

@One2OneConn[patient]("patient")
@ToStringMacro
class bind_course_region_goods_time_patient() extends commonEntity {
    var course_id: String = ""
    var region_id: String = ""
    var goods_id: String = ""
    var time_type: String = ""
    var time: String = ""
    var patient_id: String = ""
}