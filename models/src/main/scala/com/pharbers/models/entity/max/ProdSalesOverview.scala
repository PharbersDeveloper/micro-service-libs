package com.pharbers.models.entity.max

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, ToStringMacro}

@ToStringMacro
//@One2ManyConn[ProdSalesValue]("ProdSalesValue")
class ProdSalesOverview extends commonEntity {
    var title = ""
    var subtitle = ""
    var timeStart = ""
    var timeOver = ""
    var area = ""
}
