package com.pharbers.models.entity.max

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class Tips extends commonEntity {
    def this(key: String, value: String, unit: String){
        this()
        this.key = key
        this.value = value
        this.unit = unit
    }
    var key = ""
    var value = ""
    var unit = ""
}
