package com.pharbers.models.entity

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
class dest() extends commonEntity {
    var form: String = ""
    var hosp_name: String = ""
    var hosp_image: String = ""
    var hosp_category: String = ""
    var hosp_level: String = ""
    var init_time: Int = 0
    var client_grade: String = ""
    var beds: Long = 0L
    var department: String = ""
    var focus_department: String = ""
    var featured_outpatient: String = ""
    var academic_acceptance_rate: String = ""
    var academic_influence: String = ""
    var patients_distribution_department: String = ""
    var outpatient_yearly: Int = 0
    var inpatient_yearly: Int = 0
    var surgery_yearly: Int = 0
    var patients_payment_capacity: String = ""
    var drug_intake: String = ""
}