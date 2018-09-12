package com.pharbers.models.service

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting._

@ToStringMacro
class dropdown_layout() extends commonEntity {
    var whichpage: String = ""
    var text: String = ""
}