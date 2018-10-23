package com.pharbers.models.entity.max

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, ToStringMacro}

@ToStringMacro
@One2ManyConn[Ranking]("Ranking")
class ProvinceLevelRank extends commonEntity {
    var title = "provinceLevelRank"
    var unit = ""
}
