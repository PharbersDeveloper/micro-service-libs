package com.pharbers.models.entity.apm.teacher

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2OneConn, ToStringMacro}
import com.pharbers.models.entity.auth.user
import com.pharbers.models.entity.course

@ToStringMacro
@One2OneConn[user]("student")
@One2OneConn[course]("course")
class bind_teacher_student_time_paper() extends commonEntity {
    var teacher_id: String = ""
    var student_id: String = ""
    var time: Long = 0L
    var paper_id: String = ""
}