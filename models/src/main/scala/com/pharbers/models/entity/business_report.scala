package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class business_report() extends commonEntity {
    var title: String = ""
    var description: String = ""
}