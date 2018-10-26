package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class ProdSalesOverview extends commonEntity {
    var title = "产品销售额"
    var timeStart = ""
    var timeOver = ""
    var curMoSales = 0.0
    var yearYear = 0.0
    var ring = 0.0
    var totle = 0.0
    var ave = 0.0
}
