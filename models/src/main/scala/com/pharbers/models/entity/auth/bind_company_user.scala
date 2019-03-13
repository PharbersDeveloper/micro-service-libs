package com.pharbers.models.entity.auth

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class bind_company_user() extends commonEntity   {
    var company_id: String = ""
    var user_id: String = ""
}
