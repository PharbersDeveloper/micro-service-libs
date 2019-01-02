package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class apm_unit_report extends commonEntity {
    var unit: Double = 0.0
    var growth: Double = 0.0
    var contri: Double = 0.0
    var contri_index: Double = 0.0
    var share: Double = 0.0
    var share_change: Double = 0.0
    var company_target: Double = 0.0
    var achieve_rate: Double = 0.0
    var potential: Double = 0.0
    var potential_growth: Double = 0.0
    var potential_contri: Double = 0.0
}
