package com.pharbers.models.entity.auth

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class bind_user_product extends commonEntity   {
    var user_id: String = ""
    var product_id: String = ""
}
