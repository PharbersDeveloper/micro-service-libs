package com.pharbers.models.entity.max
import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class MostCard extends commonEntity {
    var title = ""
    var subtitle = ""
    var area = ""
    var name = ""
    var subname = ""
    var tag = ""
    var value = 0.0
    var percent = 0.0
    def this(title:String = "", subtitle: String = "", area: String = "", name: String = "", subname: String = "",
             tag: String = "", value: Double = 0.0, percent: Double = 0.0){
        this()
        this.title = title
        this.subname = subname
        this.area = area
        this.name = name
        this.subname = subname
        this.tag = tag
        this.value = value
        this.percent = percent
    }
}
