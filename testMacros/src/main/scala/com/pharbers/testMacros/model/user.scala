package com.pharbers.testMacros.model

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
case class user() extends commonEntity {
    var user_name: String = ""
    var user_phone: String = ""
    var email: String = ""
    var password: String = ""
}