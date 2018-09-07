package com.pharbers.models

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2OneConn, ToStringMacro}

@One2OneConn[user]("user")
@ToStringMacro
class auth() extends commonEntity {
    var token: String = ""
    var token_expire: Int = 24 * 60 * 60
}
