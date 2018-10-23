package com.pharbers.models.entity.max

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, One2OneConn, ToStringMacro}

@ToStringMacro
@One2ManyConn[ProdContValue]("Pie")
@One2OneConn[ProdSalesOverview]("ProdSalesOverview")
class ProvinceMarketPart extends commonEntity {
    var title = "各产品销售概况"
    var subtitle = ""
}
