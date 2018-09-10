package com.pharbers.models.service

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, ToStringMacro}
import com.pharbers.models.entity.medicine

/**
  * @ ProjectName models.com.pharbers.models.service.medicsnotice
  * @ author jeorch
  * @ date 18-9-7
  * @ Description: TODO
  */
@One2ManyConn[medicine]("medicines")
@One2ManyConn[notice]("notices")
@ToStringMacro
class medicsnotice() extends commonEntity  {
    var temp: String = ""
}
