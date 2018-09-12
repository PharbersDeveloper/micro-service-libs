package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class tm_phase_report extends commonEntity {
    type report = Map[String, List[Any]]

    var uuid: String = ""
    var timestamp: Int = 0
    var summary_report: report = Map().empty
    var dests_goods_report: report = Map().empty
    var rep_goods_report: report = Map().empty
    var reso_allocation_report: report = Map().empty
    var rep_ind_resos: report = Map().empty
    var rep_ability_report: report = Map().empty
    var compete_report: List[Map[String, Any]] = Nil
}
