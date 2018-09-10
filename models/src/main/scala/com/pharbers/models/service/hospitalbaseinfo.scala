package com.pharbers.models.service

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting._
import com.pharbers.models.entity.{hospital, representative}

@One2OneConn[hospital]("hospital")
@One2ManyConn[hospmedicinfo]("hospmedicinfos")
@One2OneConn[representative]("representative")
@ToStringMacro
class hospitalbaseinfo() extends commonEntity {
    var major = 1
    var minor = 0
}
