package com.pharbers.models.service

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class evaluation_line_intro_data extends commonEntity {
    var title: String = ""
    var desc: List[String] = Nil
}
