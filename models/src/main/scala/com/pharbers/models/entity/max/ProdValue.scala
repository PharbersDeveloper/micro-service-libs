package com.pharbers.models.entity.max

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class ProdValue extends commonEntity {
    var ym = ""
    var value = 0.0
    var unit = ""
}
