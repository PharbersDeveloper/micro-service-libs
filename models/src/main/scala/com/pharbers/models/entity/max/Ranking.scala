package com.pharbers.models.entity.max

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class Ranking extends commonEntity {
    var no = 0
    var value = 0.0
    var prod = ""
    var manu = ""
    var province = ""
    var growth = 0
}
