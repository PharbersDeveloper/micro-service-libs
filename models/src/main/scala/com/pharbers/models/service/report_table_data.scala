package com.pharbers.models.service

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting._

@ToStringMacro
class report_table_data() extends commonEntity {
    var columns: List[Map[String, Any]] = Nil
    var columnsValue: List[Map[String, Any]] = Nil
}