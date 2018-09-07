package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class resource() extends commonEntity {
    var form: String = ""
    var name: String = ""
    var des: String = ""
}