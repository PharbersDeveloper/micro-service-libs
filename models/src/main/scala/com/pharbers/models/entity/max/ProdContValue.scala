package com.pharbers.models.entity.max

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, ToStringMacro}

@ToStringMacro
@One2ManyConn[TipDetail]("TipDetail")
class ProdContValue extends commonEntity {
    var showValue = 0.0
    var showUnit = ""
    var title = ""
    var color = ""
//    val tips = List(Map(("key","销售额"),("value",""),("unit","mil")),Map(("key","贡献度"),("value",""),("unit","%")))
}
