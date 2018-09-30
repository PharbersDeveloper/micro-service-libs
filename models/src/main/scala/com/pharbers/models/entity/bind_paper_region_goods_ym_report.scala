package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2OneConn, ToStringMacro}

@One2OneConn[apm_report]("report")
@ToStringMacro
class bind_paper_region_goods_ym_report() extends commonEntity {
    var paper_id: String = ""
    var region_id: String = ""
    var goods_id: String = ""
    var ym: String = ""
    var report_id: String = ""
}