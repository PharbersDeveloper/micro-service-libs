package com.pharbers.models.service

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class capablity_card_ability extends commonEntity {
    var title: String = ""
    var level: String = ""
    var desc: List[Any] = Nil
}