package com.pharbers.models.service

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2OneConn, ToStringMacro}

@One2OneConn[hospdecision]("hospitaldecison")
@One2OneConn[mgrdecision]("managerdecision")
@ToStringMacro
class alldecision() extends commonEntity {
    var component_name: String = ""
    var major: Int = 1
    var minor: Int = 0
}
