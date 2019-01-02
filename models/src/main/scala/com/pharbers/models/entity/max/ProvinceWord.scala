package com.pharbers.models.entity.max

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class ProvinceWord extends commonEntity{
    var title = ""
    var subtitle = ""
    var name = ""
    var tag = ""
    var value = 0.0
    var percent = 0.0

    def this(title: String, subtitle: String, name: String, tag: String, value: Double, percent: Double){
        this()
        this.title = title
        this.subtitle = subtitle
        this.name = name
        this.tag = tag
        this.value = value
        this.percent = percent
    }
}
