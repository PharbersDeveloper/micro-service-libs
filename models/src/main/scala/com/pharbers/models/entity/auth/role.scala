package com.pharbers.models.entity.auth

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class role() extends commonEntity {
    var name: String = ""
    var des: String = ""
}
