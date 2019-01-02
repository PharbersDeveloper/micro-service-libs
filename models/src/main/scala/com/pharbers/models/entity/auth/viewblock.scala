package com.pharbers.models.entity.auth

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, ToStringMacro}

@One2ManyConn[viewblock]("subs")
@ToStringMacro
class viewblock() extends commonEntity {
    var component_name: String = ""
    var text: String = ""
}
