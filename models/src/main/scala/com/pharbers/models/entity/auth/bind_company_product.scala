package com.pharbers.models.entity.auth

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class bind_company_product() extends commonEntity {
    var company_id: String = ""
    var product_id: String = ""
}
