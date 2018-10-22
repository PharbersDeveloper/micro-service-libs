package com.pharbers.models.entity.max

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class Pie extends commonEntity {
    var prod = ""
    var market = ""
    var sales = 0.0
    var cont = 0.0
    var contMonth = 0.0
    var contSeason = 0.0
    var contYear = 0.0
    var color = ""
}
