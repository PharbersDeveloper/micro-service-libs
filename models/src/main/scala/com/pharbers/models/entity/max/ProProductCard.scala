package com.pharbers.models.entity.max

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class ProProductCard extends commonEntity {
    var title = ""
    var subtitle = ""
    var province = ""
    var leaftitle = ""
    var tag = ""
    var name = ""
    var subname = ""
    var value = 0.0
    var percent = 0.0

    def this(title: String, subtitle: String, province: String, leaftitle: String, tag: String,
             name: String, subname: String, value: Double, percent: Double){
        this()
        this.title = title
        this.subtitle = subtitle
        this.province = province
        this.leaftitle = leaftitle
        this.tag = tag
        this.name = name
        this.subname = subname
        this.value = value
        this.percent = percent
    }
}
