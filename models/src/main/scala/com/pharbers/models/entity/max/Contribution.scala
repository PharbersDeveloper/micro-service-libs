package com.pharbers.models.entity.max

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, One2OneConn, ToStringMacro}

@ToStringMacro
@One2ManyConn[Pie]("Pie")
@One2ManyConn[ProdContValue]("ProdContValue")
@One2OneConn[ProdSalesOverview]("ProdSalesOverview")
class Contribution extends commonEntity {
    var title = "contribution"
}
