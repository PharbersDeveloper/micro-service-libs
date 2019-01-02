package com.pharbers.models.entity.max

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, One2OneConn, ToStringMacro}

@ToStringMacro
@One2ManyConn[Ranking]("Ranking")
class NationProductRank extends  commonEntity {
    var title = "nationProductRank"
    var subtitle = ""
    var unit = ""
}
