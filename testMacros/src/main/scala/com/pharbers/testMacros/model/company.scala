package com.pharbers.testMacros.model

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, ToStringMacro}

@One2ManyConn[user]("users")
@ToStringMacro
class company() extends commonEntity {
    var company_name: String = ""
}
