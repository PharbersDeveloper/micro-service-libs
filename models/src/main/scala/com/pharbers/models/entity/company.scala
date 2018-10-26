package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class company() extends commonEntity {
    var name: String = ""
    var phone: String = ""
    var email: String = ""
    var image: String = ""
    var des: String = ""
}
