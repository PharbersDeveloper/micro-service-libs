package com.pharbers.models.service

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2OneConn, ToStringMacro}

@ToStringMacro
case class call_result() extends commonEntity {
    var state: Boolean = false
    var des: String = ""
}
