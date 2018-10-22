package com.pharbers.models.entity.max

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class Card extends commonEntity{
    var title = ""
    var subtitle = ""
    var tar = "tar"
    var name = ""
    var subname = ""
    var unit = ""
    var value = 0.0
    var percent = 0.0
}
