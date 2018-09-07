package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

/**
  * @ ProjectName models.com.pharbers.models.entity.news
  * @ author jeorch
  * @ date 18-9-7
  * @ Description: TODO
  */
@ToStringMacro
class notice extends commonEntity {
    var news: String = ""
}
