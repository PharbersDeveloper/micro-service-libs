package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class ProdSalesTable extends commonEntity {
    var ym = ""
    var sales = 0.0
}
