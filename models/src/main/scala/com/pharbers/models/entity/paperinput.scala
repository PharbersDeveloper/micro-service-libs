package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity

class paperinput() extends commonEntity {
    var paper_id: String = ""
    var region_id: String = ""
    var hint: String = ""
    var sorting: String = ""
    var predicted_target: Long = 0L
    var field_work_days: Int = 0
    var national_meeting: Int = 0
    var city_meeting: Int = 0
    var depart_meeting: Int = 0
    var action_plans: List[String] = Nil
}