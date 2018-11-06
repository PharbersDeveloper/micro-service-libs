package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class patient() extends commonEntity {
    var patient_num: Double = 0.0
    var patient_num_contri: Double = 0.0
}