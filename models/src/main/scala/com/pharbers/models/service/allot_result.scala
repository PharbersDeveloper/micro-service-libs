package com.pharbers.models.service

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, One2OneConn, ToStringMacro}

@One2ManyConn[hospitalbaseinfo]("hospitalbaseinfo")
@One2ManyConn[repinputinfo]("representative")
@One2OneConn[managerinputinfo]("managerinputinfo")
@ToStringMacro
class allot_result extends commonEntity {
    var uuid: String = ""
}