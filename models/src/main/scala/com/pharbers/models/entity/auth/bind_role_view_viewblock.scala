package com.pharbers.models.entity.auth

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class bind_role_view_viewblock extends commonEntity   {
    var role_id: String = ""
    var view: String = ""
    var viewblock_id: String = ""
}
