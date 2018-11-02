package com.pharbers.models.entity.auth

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class bind_viewblock_viewblock extends commonEntity   {
    var parent: String = ""
    var sub: String = ""
}
