package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class region() extends commonEntity {
    var name: String = ""
    var image: String = ""
    var describe: String = ""
}