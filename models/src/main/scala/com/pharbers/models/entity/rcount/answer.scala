package com.pharbers.models.entity.rcount

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class answer extends commonEntity {
    var course_id = ""
    var action_plans: List[String] = Nil
    var curve_name = ""
    var national_meeting: Int = 0
    var city_meeting: Int = 0
    var depart_meeting: Int = 0
    var sorting: String = ""
    var predicted_target: Long = 0L
    var allocated_target: Long = 0L
    var field_work_days: Int = 0
    var hint = ""
    var region_id = ""
}
