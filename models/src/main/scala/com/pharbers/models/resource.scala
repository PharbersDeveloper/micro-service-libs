package com.pharbers.models

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
case class resource() extends commonEntity {
    var form: String = ""
    var name: String = ""
    var des: String = ""
}