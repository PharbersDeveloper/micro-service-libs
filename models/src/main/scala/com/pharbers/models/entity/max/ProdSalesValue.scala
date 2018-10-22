package com.pharbers.models.entity.max

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class ProdSalesValue extends commonEntity{
    var prod = ""
    var market = ""
    var market_scale = 0.0
    var market_growth = 0.0
    var sales = 0.0
    var sales_growth = 0.0
    var ev_value = 0.0
    var share = 0.0
    var share_growth = 0.0
    var manufacturer = ""

}
