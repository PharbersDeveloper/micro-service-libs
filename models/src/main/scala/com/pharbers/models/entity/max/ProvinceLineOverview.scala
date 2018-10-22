package com.pharbers.models.entity.max

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, ToStringMacro}

@ToStringMacro
@One2ManyConn[MixedGraphData]("MixedGraphData")
class ProvinceLineOverview extends commonEntity {
    var title = "市场各省份销售概况"
}
