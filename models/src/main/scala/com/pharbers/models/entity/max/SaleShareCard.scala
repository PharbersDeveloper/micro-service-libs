package com.pharbers.models.entity.max

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class SaleShareCard extends commonEntity {
    def this(title: String, subtitle: String, area: String,leaftitle: String, num: Double, tag: String, yearOnYear: Double, ringRatio: Double){
        this()
        this.title = title
        this.subtitle = subtitle
        this.area = area
        this.leaftitle = leaftitle
        this.num = num
        this.tag = tag
        this.yearOnYear = yearOnYear
        this.ringRatio = ringRatio
    }
    var title = ""
    var subtitle = ""
    var area = ""
    var leaftitle = ""
    var num = 0.0
    var tag = ""
    var yearOnYear = 0.0
    var ringRatio = 0.0
}
