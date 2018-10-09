package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class apmquarterreport() extends commonEntity {
    var worst_sales: Long = 0L
    var best_sales: Long = 0L
    var pre_sales: Long = 0L
    var worst_share: Double = 0.0
    var best_share: Double = 0.0
    var pre_share: Double = 0.0
}