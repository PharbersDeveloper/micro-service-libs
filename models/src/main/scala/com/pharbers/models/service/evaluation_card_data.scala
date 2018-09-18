package com.pharbers.models.service

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, ToStringMacro}

@One2ManyConn[capablity_card_ability]("ability")
@ToStringMacro
class evaluation_card_data extends commonEntity {
    var title: String = ""
    var content: String = ""
}
