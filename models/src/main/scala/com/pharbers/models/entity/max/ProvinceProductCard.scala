package com.pharbers.models.entity.max

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, ToStringMacro}

@ToStringMacro
@One2ManyConn[ProProductCard]("ProProductCard")
class ProvinceProductCard extends commonEntity {
    val title = "provinceProductCard"
}
