package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class apmreport extends commonEntity {
    var potential: Double = 0.0
    var potential_growth: Double = 0.0
    var potential_contri: Double = 0.0
    var sales: Double = 0.0
    var sales_growth: Double = 0.0
    var sales_contri: Double = 0.0
    var contri_index: Double = 0.0
    var share: Double = 0.0
    var share_change: Double = 0.0
    var company_target: Double = 0.0
    var achieve_rate: Double = 0.0
}
