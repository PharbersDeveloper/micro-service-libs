package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2OneConn, ToStringMacro}

@One2OneConn[sales]("sales")
@ToStringMacro
class bind_course_region_goods_ym_sales() extends commonEntity {
    var course_id: String = ""
    var region_id: String = ""
    var goods_id: String = ""
    var ym: String = ""
    var sales_id: String = ""
}