package com.pharbers.models.entity.auth

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class user() extends commonEntity {
    var email: String = ""
    var password: String = ""
    var image: String = ""
    var user_name: String = ""
    var user_phone: String = ""
    val company_name: String = ""
    val position_name: String = ""
}
