package com.pharbers.models.entity.max

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, ToStringMacro}

@ToStringMacro
@One2ManyConn[Market]("Market")
class AllMarket extends commonEntity {
    var title = "all market"
    var status = "ok"
}
