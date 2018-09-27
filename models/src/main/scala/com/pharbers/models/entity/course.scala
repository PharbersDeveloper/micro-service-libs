package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, ToStringMacro}

@One2ManyConn[medicine]("med_lst")
@ToStringMacro
class course() extends commonEntity {
    var name: String = ""
    var state: Boolean = false
    var prompt: String = ""
    var describe: String = ""
}