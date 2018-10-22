package com.pharbers.models.entity.max

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, ToStringMacro}

@ToStringMacro
@One2ManyConn[MostCard]("MostCard")
class NationMostWord extends commonEntity {
    var title = "nationMostWord"
}
