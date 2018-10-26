package com.pharbers.models.entity.max

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, ToStringMacro}

@One2ManyConn[ProdValue]("ProdValue")
@ToStringMacro
class ProdTrendLine extends commonEntity {
    var name = ""
}
