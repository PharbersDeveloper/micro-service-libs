package com.pharbers.models.entity.max

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, One2OneConn, ToStringMacro}

@ToStringMacro
class ProvinceWord extends commonEntity{
    var title = ""
    var subtitle = ""
    var name = ""
    var tag = ""
    var value = 0.0
    var percent = 0.0
}
