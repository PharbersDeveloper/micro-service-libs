package com.pharbers.models.entity.auth

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class bind_company_secret() extends commonEntity {
    var company_id: String = ""
    var secret_id: String = ""
}
