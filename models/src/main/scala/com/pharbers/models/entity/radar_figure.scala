package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class radar_figure() extends commonEntity {
    var call_times_val: Int = 0
    var in_field_days_val: Int = 0
    var motivation_val: Int = 0
    var prod_knowledge_val: Int = 0
    var sales_skills_val: Int = 0
    var target_call_freq_val: Int = 0
    var territory_manage_val: Int = 0
}