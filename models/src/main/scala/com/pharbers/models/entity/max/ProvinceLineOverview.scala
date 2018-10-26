package com.pharbers.models.entity.max

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, One2OneConn, ToStringMacro}

@ToStringMacro
@One2ManyConn[MixedGraphLine]("MixedGraphLine")
@One2OneConn[ProdSalesOverview]("ProdSalesOverview")
class ProvinceLineOverview extends commonEntity {
    var title = "provinceLineOverview"
}
