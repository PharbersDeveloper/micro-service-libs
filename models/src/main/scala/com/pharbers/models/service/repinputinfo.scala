package com.pharbers.models.service

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2OneConn, ToStringMacro}
import com.pharbers.models.entity.representative

@One2OneConn[representative]("repInfo")
@ToStringMacro
class repinputinfo extends commonEntity {
    val intro: String = ""
    val total_days: Int = 0
    val used_days: Int = 0
    val team_meet: Int = 0
    val product_train: Int = 0
    val sales_train: Int = 0
}