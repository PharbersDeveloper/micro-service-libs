package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, One2OneConn, ToStringMacro}

@One2OneConn[ProdSalesOverview]("ProdSalesOverview")
@One2ManyConn[ProdSalesTable]("ProdSalesTable")
@ToStringMacro
class TableSale extends commonEntity {
    var title = "产品销售额表头"
}
