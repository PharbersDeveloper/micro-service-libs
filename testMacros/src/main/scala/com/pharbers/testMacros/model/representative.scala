package com.pharbers.testMacros.model

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
case class representative() extends commonEntity {
    var rep_name: String = ""
    var rep_image: String = ""
    var rep_level: String = ""
    var age: Int = 0
    var education: String = ""
    var profe_bg: String = ""
    var service_year: Int = 0
    var entry_time: Int = 0
    var business_exp: String = ""
    var advantage: String = ""
    var weakness: String = ""
    var sales_skills_val: Int = 0
    var prod_knowledge_val: Int = 0
    var motivation_val: Int = 0
    var overall_val: Int = 0
}