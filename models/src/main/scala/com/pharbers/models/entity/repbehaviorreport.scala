package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class repbehaviorreport() extends commonEntity {
    var target_call_freq_val: Int = 0
    var in_field_days_val: Int = 0
    var call_times_val: Int = 0
    var target_occupation_val: Int = 0
}