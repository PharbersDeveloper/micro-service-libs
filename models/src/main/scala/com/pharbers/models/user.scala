package com.pharbers.models

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class user() extends commonEntity {
    var user_name: String = ""
    var user_phone: String = ""
    var email: String = ""
    var password: String = ""
}
