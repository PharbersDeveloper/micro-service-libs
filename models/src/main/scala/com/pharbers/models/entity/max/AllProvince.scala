package com.pharbers.models.entity.max

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, ToStringMacro}

@ToStringMacro
@One2ManyConn[Province]("Province")
class AllProvince extends commonEntity {
    var title = "allProvince"
    var status = "ok"
}
