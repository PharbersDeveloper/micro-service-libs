package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2OneConn, ToStringMacro}

@One2OneConn[course]("course")
@ToStringMacro
class paper() extends commonEntity {
    var state: Boolean = false
    var start_time: Long = 0
    var end_time: Long = 0
}