package com.pharbers.models.service

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, One2OneConn, ToStringMacro}
import com.pharbers.models.entity.auth.{company, product, role, user}

@One2OneConn[user]("user")
@One2OneConn[company]("company")
@One2ManyConn[role]("role")
@One2ManyConn[product]("product")
@ToStringMacro
class auth() extends commonEntity {
    var token: String = ""
    var token_expire: Int = 24 * 60 * 60
}
