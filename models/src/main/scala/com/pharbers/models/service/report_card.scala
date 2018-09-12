package com.pharbers.models.service

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting._

@ToStringMacro
class report_card() extends commonEntity {
    var index: Int = 0
    var title: String = ""
    var form: String = ""
    var value: Any = null
    var ext: Any = null
}