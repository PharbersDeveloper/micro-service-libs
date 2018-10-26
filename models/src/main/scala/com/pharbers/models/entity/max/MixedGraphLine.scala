package com.pharbers.models.entity.max

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class MixedGraphLine extends commonEntity {
    var province = ""
    var scale = 0.0
    var market_growth = 0.0
    var sales = 0.0
    var prod_growth = 0.0
}
