package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class secret() extends commonEntity {
    var private_key: String = ""
    var public_key: String = ""
}
