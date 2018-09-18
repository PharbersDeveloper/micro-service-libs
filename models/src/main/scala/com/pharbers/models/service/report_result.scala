package com.pharbers.models.service

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class report_result extends commonEntity {
    var outcome: List[String] = Nil
    var report_id: List[String] = Nil
    var assess_report_id: List[String] = Nil
}