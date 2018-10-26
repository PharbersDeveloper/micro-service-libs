package com.pharbers.models.entity.max

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class MultipleLine extends commonEntity {
    var ym = ""
    var marketSales = 0.0
    var prodSales = 0.0
    var share = 0.0
}
