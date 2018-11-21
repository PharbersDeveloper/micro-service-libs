package com.pharbers.models.entity.apm.teacher

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.One2ManyConn
import com.pharbers.models.entity.{bind_paper_region_goods_time_report, paperinput}

@One2ManyConn[paperinput]("inputLst")
@One2ManyConn[bind_paper_region_goods_time_report]("reportLst")
class download_paper() extends commonEntity {
    var tmp: Long = 0L
}
