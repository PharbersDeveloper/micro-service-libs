package com.pharbers.models

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class bind_user_proposal() extends commonEntity {
    var user_id: String = ""
    var proposal_id: String = ""
}
